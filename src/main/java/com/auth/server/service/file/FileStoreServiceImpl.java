package com.auth.server.service.file;

import com.auth.server.models.Channel;
import com.auth.server.models.User;
import com.auth.server.models.Video;
import com.auth.server.models.VideoEncodingSyncStatusDTO;
import com.auth.server.repository.UserRepository;
import com.auth.server.repository.VideoRepository;
import com.auth.server.security.jwt.JwtUtils;
import com.auth.server.service.encoding.VideoEncodingService;
import com.auth.server.service.user.UserDetailsImpl;
import com.auth.server.utils.activemq.VideoEncodingEvent;
import com.auth.server.utils.encrption.EncryptionUtils;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 20/02/22.
 */
@Service
public class FileStoreServiceImpl implements FileStoreService {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private static final String baseUrl = "http://192.168.64.2/hls/";
    private static final String baseUrlSuffix = "/video/master.m3u8";
    private static final String video_path= "/Users/admin/.bitnami/stackman/machines/xampp/volumes/root/htdocs/hls/";
    private static final String processing_path = "/Users/admin/.bitnami/stackman/machines/xampp/volumes/root/htdocs/videoProcessing/";
    private static final String ffmpeg="ffmpeg ";
    private  static final String thumbnailFileType = "jpg";
    @Autowired UserRepository userRepository;
    @Autowired VideoRepository videoRepository;
    @Autowired JmsTemplate jmsTemplate;
    @Autowired VideoEncodingService videoEncodingService;
    @Override
    public ResponseEntity<?> storeAndEncodeFile(MultipartFile inputVideo) {
        logger.info("storeAndEncodeFile method called for inpute file :"+inputVideo.getOriginalFilename());
        Video video = new Video();
        String finalVideoUrl = null;
        String videoName = inputVideo.getOriginalFilename().replaceAll("\\s+", "");;
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        Optional<User> user = userRepository.findByUsername(userName);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateInString = dateFormat.format(date);
        String videoExt = FilenameUtils.getExtension(videoName);
        Boolean supportedExtenstion = validateExtension(videoExt);

        if(!supportedExtenstion) {
            logger.info("Given file format is not suported :" + videoName);
        } else {
            File destinationFile = new File(processing_path, videoName);
            try {
                inputVideo.transferTo(destinationFile);
                logger.info("Given file transfer to processing path Successfully!!!");
                VideoEncodingEvent videoEncodingEvent = new VideoEncodingEvent();
                String modifiedVideoPath= processing_path+videoName;
                logger.info("Original File name :"+videoName+" | File store path :"+modifiedVideoPath);
                videoEncodingEvent.setVideoPath(modifiedVideoPath);
                videoEncodingEvent.setUesrname(userName);
                videoEncodingEvent.setVideoDuration(Double.parseDouble(findLengthOfVideo(modifiedVideoPath).toString()));
                videoEncodingEvent.setVideoSize(inputVideo.getSize());
                videoEncodingEvent.setVideoName(videoName);
                videoEncodingEvent.setUser(user.get());

                String requestId = EncryptionUtils.base64Encode(videoName+":"+userName+":"+dateInString);
                VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO = videoEncodingService.getVideoEncodingStatus(requestId);
                System.out.println(requestId);
                videoEncodingEvent.setRequestId(requestId);
                if(Channel.SyncExecutionStatus.IDLE.equals(videoEncodingSyncStatusDTO.getSyncExecutionStatus())){
                    videoEncodingSyncStatusDTO.setSyncExecutionStatus(Channel.SyncExecutionStatus.QUEUED);
                    videoEncodingService.updateVideoEncodingStatus(videoEncodingSyncStatusDTO);
                }
                String json = new Gson().toJson(videoEncodingEvent);
                jmsTemplate.send("video-encode", new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage();
                        textMessage.setText(json);
                        return textMessage;
                    }
                });
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ResponseEntity<?> runVideoEncoding(VideoEncodingEvent videoEncodingEvent) throws  IOException{
        Video video = new Video();
        VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO = videoEncodingService.getVideoEncodingStatus(videoEncodingEvent.getRequestId());
        videoEncodingSyncStatusDTO.setSyncExecutionStatus(Channel.SyncExecutionStatus.RUNNING);
        videoEncodingService.updateVideoEncodingStatus(videoEncodingSyncStatusDTO);
        String finalVideoUrl = encodeVideo(videoEncodingEvent);
        String finalVideoThumbnailUrl = generateThumbnail(videoEncodingEvent);
        videoEncodingSyncStatusDTO.setSyncExecutionStatus(Channel.SyncExecutionStatus.IDLE);
        videoEncodingService.updateVideoEncodingStatus(videoEncodingSyncStatusDTO);
        video.setVideoSize(videoEncodingEvent.getVideoSize());
        video.setVideoDuration(videoEncodingEvent.getVideoDuration());
        video.setEnable(true);
        video.setThumbUrl(finalVideoThumbnailUrl);
        video.setVideoUrl(finalVideoUrl);
        video.setUser(videoEncodingEvent.getUser());
        videoRepository.save(video);
        return null;
    }
    @Override public List<Video> fetchVideosByUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        Optional<User> user = userRepository.findByUsername(userName);

        return videoRepository.findByUser_id(Long.parseLong(user.get().getId().toString()));
    }

    @Override public List<Video> fetchVideos() {

        return videoRepository.findAll();
    }

    public String  generateThumbnail(VideoEncodingEvent videoEncodingEvent) throws IOException{
        String thumbnailUrl = null;
        String userName = videoEncodingEvent.getUesrname();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateInString = dateFormat.format(date);
        String inFilename = videoEncodingEvent.getVideoPath();
        String videoName = videoEncodingEvent.getVideoName();
        File fileLocation = new File(video_path + "/" + userName + "/");
        if (!fileLocation.exists()) {

            if (fileLocation.mkdirs()) {
                logger.info("sub directories created successfully" + fileLocation.getPath());
            }
            else {
                logger.info("failed to create sub directories");
            }
        }
        String fileNameWithoudExtension = videoName.substring(0, videoName.lastIndexOf('.'));
        File outFileName = new File(video_path + "/" + userName + "/" + fileNameWithoudExtension + "/thumbnail/");
        logger.info("thumbnail path :"+outFileName);
        if (!outFileName.exists()) {

            if (outFileName.mkdirs()) {
                logger.info("sub directories created successfully" + outFileName.getPath());
            }
            else {
                logger.info("failed to create sub directories");
            }
        }

        int frameAtSecond=0;
        Double videoLength = 0.0;
        // videoLengthInString=null;
        int frameAtMinute = 0;

        videoLength=findLengthOfVideo(inFilename);
        if (videoLength > 0.0 && videoLength <= 60.0) {
            frameAtSecond = 0;
        }
        if (videoLength > 60.0 && videoLength <= 120.0) {
            frameAtMinute = 01;
            frameAtSecond = 00;
        }
        if (videoLength > 120.0) {
            frameAtMinute = 02;
            frameAtSecond = 00;
        }


        /**
         * generate frame at videoAtSecond seconds
         */
        String command="ffmpeg -i \""+inFilename+"\" -ss 00:"+frameAtMinute+":"+frameAtSecond+".000 -vframes 1 -n \""+outFileName+"/thumbnail."+thumbnailFileType+"\" -hide_banner";
        logger.info("command :"+command);
        long start = System.currentTimeMillis();
        //Process p2 = Runtime.getRuntime().exec(command);
        try {
            System.out.println(command);
            if(runScript(command)) {
                logger.info("Operation Successfull!!!!");
            }
            else {
                logger.info("Operation Failed ####");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        logger.info("time taken to generate thumbnail is: "+(end-start) +" sec");

        logger.info("Thumbnail Done!!!");
        return baseUrl+ "/" + userName + "/" + fileNameWithoudExtension + "/thumbnail/"+"thumbnail."+thumbnailFileType;

    }
    public Double findLengthOfVideo(String videoPath) throws IOException{
            Double videoLength;
            final FFprobe ffprobe = new FFprobe();
            FFmpegProbeResult probeResult = ffprobe.probe(videoPath);
            FFmpegFormat format = probeResult.getFormat();
            videoLength = format.duration;
            logger.info("Video Duration : "+videoLength);
            return videoLength;
    }
    public String  encodeVideo(VideoEncodingEvent videoEncodingEvent) throws IOException {
        String VideoUrl = null;
        String userName = videoEncodingEvent.getUesrname();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateInString = dateFormat.format(date);
        String inFilename = videoEncodingEvent.getVideoPath();
        File fileLocation = new File(video_path + "/" + userName + "/");
        String videoName = videoEncodingEvent.getVideoName();
        String fileNameWithoudExtension = videoName.substring(0, videoName.lastIndexOf('.'));
        String outFileName = video_path + "/" + userName + "/" + fileNameWithoudExtension + "/video/";
        if (!fileLocation.exists()) {
            if (fileLocation.mkdirs()) {
                logger.info("sub directories created successfully" + fileLocation.getPath());
            }
            else {
                logger.info("failed to create sub directories");
            }
        }

        String conversion =   "ffmpeg -i " + inFilename
                + " -preset slow -g 48 -sc_threshold 0 -map 0:0 -map 0:1 -map 0:0 -map 0:1 -map 0:0 -map 0:1 -map 0:0 -map 0:1 -map 0:0 -map 0:1 -map 0:0 -map 0:1 -s:v:0 1920*1080 -b:v:0 1800k -s:v:1 1280*720 -b:v:1 1200k -s:v:2 858*480 -b:v:2 750k -s:v:3 630*360 -b:v:3 550k -s:v:4 426*240 -b:v:4 400k -s:v:5 256*144 -b:v:5 200k -c:a copy -var_stream_map \"v:0,a:0,name:1080p v:1,a:1,name:720p v:2,a:2,name:480p v:3,a:3,name:360p v:4,a:4,name:240p v:5,a:5,name:144p\" -master_pl_name master.m3u8 -f hls -hls_time 10 -hls_playlist_type vod -hls_list_size 0 -hls_segment_filename \""
                + outFileName + "%v/segment%d.ts\" " + outFileName + "%v/index.m3u8";
        //		String conversion = "cmd.exe /c F:\\java\\vocabimate\\ffmpeg\\ffmpeg\\bin\\ffmpeg -i "+original_video_file+" -hls_time 10  -hls_playlist_type vod -hls_segment_filename \"C:\\xampp\\htdocs\\hls\\hariom_test\\video_segments_%0d.ts\" C:\\xampp\\htdocs\\hls\\hariom_test\\hls_master_for_test.m3u8";
        //String conversion = "cmd.exe /c "+"dir";
        String[] cmds = { conversion };


        try {
            System.out.println(conversion);
            if (runScript(conversion)) {
                logger.info("Operation Successfull!!!!");
            }
            else {
                logger.info("Operation Failed ####");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.exit(0);



        logger.info("encoding Done!!!");
        return baseUrl+userName + "/" + fileNameWithoudExtension +baseUrlSuffix;
    }

    private static boolean runScript(String cmd) throws IOException, InterruptedException {
        boolean success = false;
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", cmd);
        try {

            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                logger.info("Executed Sucessfully :"+cmd);
                success = true;
                //System.out.println(output);
                //System.exit(0);
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  success;
    }
    private static void flushInputStreamReader (Process process) throws IOException, InterruptedException {
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line=null;
        StringBuilder s = new StringBuilder();
        while((line=input.readLine()) != null) {
            s.append(line);
        }
    }
    public Boolean validateExtension(String ext) {
        if (ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("mov") || ext.equalsIgnoreCase("WebM")
                || ext.equalsIgnoreCase("FLV") || ext.equalsIgnoreCase("MPG")) {
            return true;
        } else
            return false;
    }

}
