/*
 *  Copyright 2011 Unicommerce eSolutions (P) Limited All Rights Reserved.
 *  UNICOMMERCE ESOLUTIONS PROPRIETARYARY/CONFIDENTIAL. Use is subject to license terms.
 *  
 *  @version     1.0, Dec 12, 2011
 *  @author singla
 */
package com.auth.server.models;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.util.StringUtils;

/**
 * @author singla
 */
public class DateUtils {
    private static final Logger   LOG              = LoggerFactory.getLogger(DateUtils.class);
    public final static String    PATTERN_DDMMYYYY = "ddMMyyyy";
    private final static TimeZone IST              = TimeZone.getTimeZone("IST");
    public static final int       MINUTES_IN_A_DAY = 24 * 60;
    public static TimeZone        DEFAULT_TZ       = IST;
    public final static String    PATTERN          = "dd-MMM-yyyy HH:mm:ss";

    public static enum TextRange {
        TODAY,
        YESTERDAY,
        LAST_WEEK,
        LAST_MONTH,
        THIS_MONTH,
        LAST_7_DAYS,
        LAST_30_DAYS,
        LAST_60_DAYS,
        LAST_90_DAYS,
        LAST_QUARTER,
        THIS_QUARTER
    }

    public static enum TimeZoneId {
        MIT("MIT"),
        PACIFIC_APIA("Pacific/Apia"),
        PACIFIC_NIUE("Pacific/Niue"),
        PACIFIC_PAGO_PAGO("Pacific/Pago_Pago"),
        AMERICA_ADAK("America/Adak"),
        HST("HST"),
        PACIFIC_FAKAOFO("Pacific/Fakaofo"),
        PACIFIC_HONOLULU("Pacific/Honolulu"),
        PACIFIC_RAROTONGA("Pacific/Rarotonga"),
        PACIFIC_TAHITI("Pacific/Tahiti"),
        PACIFIC_MARQUESAS("Pacific/Marquesas"),
        AST("AST"),
        AMERICA_ANCHORAGE("America/Anchorage"),
        PACIFIC_GAMBIER("Pacific/Gambier"),
        AMERICA_LOS_ANGELES("America/Los_Angeles"),
        AMERICA_TIJUANA("America/Tijuana"),
        AMERICA_VANCOUVER("America/Vancouver"),
        PST("PST"),
        PACIFIC_PITCAIRN("Pacific/Pitcairn"),
        AMERICA_DAWSON_CREEK("America/Dawson_Creek"),
        AMERICA_DENVER("America/Denver"),
        AMERICA_EDMONTON("America/Edmonton"),
        AMERICA_MAZATLAN("America/Mazatlan"),
        AMERICA_PHOENIX("America/Phoenix"),
        MST("MST"),
        PNT("PNT"),
        AMERICA_BELIZE("America/Belize"),
        AMERICA_CHICAGO("America/Chicago"),
        AMERICA_COSTA_RICA("America/Costa_Rica"),
        AMERICA_EL_SALVADOR("America/El_Salvador"),
        AMERICA_GUATEMALA("America/Guatemala"),
        AMERICA_MANAGUA("America/Managua"),
        AMERICA_MEXICO_CITY("America/Mexico_City"),
        AMERICA_REGINA("America/Regina"),
        AMERICA_TEGUCIGALPA("America/Tegucigalpa"),
        AMERICA_WINNIPEG("America/Winnipeg"),
        CST("CST"),
        PACIFIC_EASTER("Pacific/Easter"),
        PACIFIC_GALAPAGOS("Pacific/Galapagos"),
        AMERICA_BOGOTA("America/Bogota"),
        AMERICA_CAYMAN("America/Cayman"),
        AMERICA_GRAND_TURK("America/Grand_Turk"),
        AMERICA_GUAYAQUIL("America/Guayaquil"),
        AMERICA_HAVANA("America/Havana"),
        AMERICA_INDIANAPOLIS("America/Indianapolis"),
        AMERICA_JAMAICA("America/Jamaica"),
        AMERICA_LIMA("America/Lima"),
        AMERICA_MONTREAL("America/Montreal"),
        AMERICA_NASSAU("America/Nassau"),
        AMERICA_NEW_YORK("America/New_York"),
        AMERICA_PANAMA("America/Panama"),
        AMERICA_PORT_AU_PRINCE("America/Port_au_Prince"),
        AMERICA_PORTO_ACRE("America/Porto_Acre"),
        AMERICA_RIO_BRANCO("America/Rio_Branco"),
        EST("EST"),
        IET("IET"),
        AMERICA_ANGUILLA("America/Anguilla"),
        AMERICA_ANTIGUA("America/Antigua"),
        AMERICA_ARUBA("America/Aruba"),
        AMERICA_ASUNCION("America/Asuncion"),
        AMERICA_BARBADOS("America/Barbados"),
        AMERICA_CARACAS("America/Caracas"),
        AMERICA_CUIABA("America/Cuiaba"),
        AMERICA_CURACAO("America/Curacao"),
        AMERICA_DOMINICA("America/Dominica"),
        AMERICA_GRENADA("America/Grenada"),
        AMERICA_GUADELOUPE("America/Guadeloupe"),
        AMERICA_GUYANA("America/Guyana"),
        AMERICA_HALIFAX("America/Halifax"),
        AMERICA_LA_PAZ("America/La_Paz"),
        AMERICA_MANAUS("America/Manaus"),
        AMERICA_MARTINIQUE("America/Martinique"),
        AMERICA_MONTSERRAT("America/Montserrat"),
        AMERICA_PORT_OF_SPAIN("America/Port_of_Spain"),
        AMERICA_PUERTO_RICO("America/Puerto_Rico"),
        AMERICA_SANTIAGO("America/Santiago"),
        AMERICA_SANTO_DOMINGO("America/Santo_Domingo"),
        AMERICA_ST_KITTS("America/St_Kitts"),
        AMERICA_ST_LUCIA("America/St_Lucia"),
        AMERICA_ST_THOMAS("America/St_Thomas"),
        AMERICA_ST_VINCENT("America/St_Vincent"),
        AMERICA_THULE("America/Thule"),
        AMERICA_TORTOLA("America/Tortola"),
        ANTARCTICA_PALMER("Antarctica/Palmer"),
        ATLANTIC_BERMUDA("Atlantic/Bermuda"),
        ATLANTIC_STANLEY("Atlantic/Stanley"),
        PRT("PRT"),
        AMERICA_ST_JOHNS("America/St_Johns"),
        CNT("CNT"),
        AGT("AGT"),
        AMERICA_BUENOS_AIRES("America/Buenos_Aires"),
        AMERICA_CAYENNE("America/Cayenne"),
        AMERICA_FORTALEZA("America/Fortaleza"),
        AMERICA_GODTHAB("America/Godthab"),
        AMERICA_MIQUELON("America/Miquelon"),
        AMERICA_MONTEVIDEO("America/Montevideo"),
        AMERICA_PARAMARIBO("America/Paramaribo"),
        AMERICA_SAO_PAULO("America/Sao_Paulo"),
        BET("BET"),
        AMERICA_NORONHA("America/Noronha"),
        ATLANTIC_SOUTH_GEORGIA("Atlantic/South_Georgia"),
        AMERICA_SCORESBYSUND("America/Scoresbysund"),
        ATLANTIC_AZORES("Atlantic/Azores"),
        ATLANTIC_CAPE_VERDE("Atlantic/Cape_Verde"),
        ATLANTIC_JAN_MAYEN("Atlantic/Jan_Mayen"),
        AFRICA_ABIDJAN("Africa/Abidjan"),
        AFRICA_ACCRA("Africa/Accra"),
        AFRICA_BANJUL("Africa/Banjul"),
        AFRICA_BISSAU("Africa/Bissau"),
        AFRICA_CASABLANCA("Africa/Casablanca"),
        AFRICA_CONAKRY("Africa/Conakry"),
        AFRICA_DAKAR("Africa/Dakar"),
        AFRICA_FREETOWN("Africa/Freetown"),
        AFRICA_LOME("Africa/Lome"),
        AFRICA_MONROVIA("Africa/Monrovia"),
        AFRICA_NOUAKCHOTT("Africa/Nouakchott"),
        AFRICA_OUAGADOUGOU("Africa/Ouagadougou"),
        AFRICA_SAO_TOME("Africa/Sao_Tome"),
        AFRICA_TIMBUKTU("Africa/Timbuktu"),
        ATLANTIC_CANARY("Atlantic/Canary"),
        ATLANTIC_FAEROE("Atlantic/Faeroe"),
        ATLANTIC_REYKJAVIK("Atlantic/Reykjavik"),
        ATLANTIC_ST_HELENA("Atlantic/St_Helena"),
        EUROPE_DUBLIN("Europe/Dublin"),
        EUROPE_LISBON("Europe/Lisbon"),
        EUROPE_LONDON("Europe/London"),
        GMT("GMT"),
        UTC("UTC"),
        WET("WET"),
        AFRICA_ALGIERS("Africa/Algiers"),
        AFRICA_BANGUI("Africa/Bangui"),
        AFRICA_DOUALA("Africa/Douala"),
        AFRICA_KINSHASA("Africa/Kinshasa"),
        AFRICA_LAGOS("Africa/Lagos"),
        AFRICA_LIBREVILLE("Africa/Libreville"),
        AFRICA_LUANDA("Africa/Luanda"),
        AFRICA_MALABO("Africa/Malabo"),
        AFRICA_NDJAMENA("Africa/Ndjamena"),
        AFRICA_NIAMEY("Africa/Niamey"),
        AFRICA_PORTO_NOVO("Africa/Porto_Novo"),
        AFRICA_TUNIS("Africa/Tunis"),
        AFRICA_WINDHOEK("Africa/Windhoek"),
        ECT("ECT"),
        EUROPE_AMSTERDAM("Europe/Amsterdam"),
        EUROPE_ANDORRA("Europe/Andorra"),
        EUROPE_BELGRADE("Europe/Belgrade"),
        EUROPE_BERLIN("Europe/Berlin"),
        EUROPE_BRUSSELS("Europe/Brussels"),
        EUROPE_BUDAPEST("Europe/Budapest"),
        EUROPE_COPENHAGEN("Europe/Copenhagen"),
        EUROPE_GIBRALTAR("Europe/Gibraltar"),
        EUROPE_LUXEMBOURG("Europe/Luxembourg"),
        EUROPE_MADRID("Europe/Madrid"),
        EUROPE_MALTA("Europe/Malta"),
        EUROPE_MONACO("Europe/Monaco"),
        EUROPE_OSLO("Europe/Oslo"),
        EUROPE_PARIS("Europe/Paris"),
        EUROPE_PRAGUE("Europe/Prague"),
        EUROPE_ROME("Europe/Rome"),
        EUROPE_STOCKHOLM("Europe/Stockholm"),
        EUROPE_TIRANE("Europe/Tirane"),
        EUROPE_VADUZ("Europe/Vaduz"),
        EUROPE_VIENNA("Europe/Vienna"),
        EUROPE_WARSAW("Europe/Warsaw"),
        EUROPE_ZURICH("Europe/Zurich"),
        ART("ART"),
        AFRICA_BLANTYRE("Africa/Blantyre"),
        AFRICA_BUJUMBURA("Africa/Bujumbura"),
        AFRICA_CAIRO("Africa/Cairo"),
        AFRICA_GABORONE("Africa/Gaborone"),
        AFRICA_HARARE("Africa/Harare"),
        AFRICA_JOHANNESBURG("Africa/Johannesburg"),
        AFRICA_KIGALI("Africa/Kigali"),
        AFRICA_LUBUMBASHI("Africa/Lubumbashi"),
        AFRICA_LUSAKA("Africa/Lusaka"),
        AFRICA_MAPUTO("Africa/Maputo"),
        AFRICA_MASERU("Africa/Maseru"),
        AFRICA_MBABANE("Africa/Mbabane"),
        AFRICA_TRIPOLI("Africa/Tripoli"),
        ASIA_AMMAN("Asia/Amman"),
        ASIA_BEIRUT("Asia/Beirut"),
        ASIA_DAMASCUS("Asia/Damascus"),
        ASIA_JERUSALEM("Asia/Jerusalem"),
        ASIA_NICOSIA("Asia/Nicosia"),
        CAT("CAT"),
        EET("EET"),
        EUROPE_ATHENS("Europe/Athens"),
        EUROPE_BUCHAREST("Europe/Bucharest"),
        EUROPE_CHISINAU("Europe/Chisinau"),
        EUROPE_HELSINKI("Europe/Helsinki"),
        EUROPE_ISTANBUL("Europe/Istanbul"),
        EUROPE_KALININGRAD("Europe/Kaliningrad"),
        EUROPE_KIEV("Europe/Kiev"),
        EUROPE_MINSK("Europe/Minsk"),
        EUROPE_RIGA("Europe/Riga"),
        EUROPE_SIMFEROPOL("Europe/Simferopol"),
        EUROPE_SOFIA("Europe/Sofia"),
        EUROPE_TALLINN("Europe/Tallinn"),
        EUROPE_VILNIUS("Europe/Vilnius"),
        AFRICA_ADDIS_ABABA("Africa/Addis_Ababa"),
        AFRICA_ASMERA("Africa/Asmera"),
        AFRICA_DAR_ES_SALAAM("Africa/Dar_es_Salaam"),
        AFRICA_DJIBOUTI("Africa/Djibouti"),
        AFRICA_KAMPALA("Africa/Kampala"),
        AFRICA_KHARTOUM("Africa/Khartoum"),
        AFRICA_MOGADISHU("Africa/Mogadishu"),
        AFRICA_NAIROBI("Africa/Nairobi"),
        ASIA_ADEN("Asia/Aden"),
        ASIA_BAGHDAD("Asia/Baghdad"),
        ASIA_BAHRAIN("Asia/Bahrain"),
        ASIA_KUWAIT("Asia/Kuwait"),
        ASIA_QATAR("Asia/Qatar"),
        ASIA_RIYADH("Asia/Riyadh"),
        EAT("EAT"),
        EUROPE_MOSCOW("Europe/Moscow"),
        INDIAN_ANTANANARIVO("Indian/Antananarivo"),
        INDIAN_COMORO("Indian/Comoro"),
        INDIAN_MAYOTTE("Indian/Mayotte"),
        ASIA_TEHRAN("Asia/Tehran"),
        MET("MET"),
        ASIA_AQTAU("Asia/Aqtau"),
        ASIA_BAKU("Asia/Baku"),
        ASIA_DUBAI("Asia/Dubai"),
        ASIA_MUSCAT("Asia/Muscat"),
        ASIA_TBILISI("Asia/Tbilisi"),
        ASIA_YEREVAN("Asia/Yerevan"),
        EUROPE_SAMARA("Europe/Samara"),
        INDIAN_MAHE("Indian/Mahe"),
        INDIAN_MAURITIUS("Indian/Mauritius"),
        INDIAN_REUNION("Indian/Reunion"),
        NET("NET"),
        ASIA_KABUL("Asia/Kabul"),
        ASIA_AQTOBE("Asia/Aqtobe"),
        ASIA_ASHGABAT("Asia/Ashgabat"),
        ASIA_ASHKHABAD("Asia/Ashkhabad"),
        ASIA_BISHKEK("Asia/Bishkek"),
        ASIA_DUSHANBE("Asia/Dushanbe"),
        ASIA_KARACHI("Asia/Karachi"),
        ASIA_TASHKENT("Asia/Tashkent"),
        ASIA_YEKATERINBURG("Asia/Yekaterinburg"),
        INDIAN_CHAGOS("Indian/Chagos"),
        INDIAN_KERGUELEN("Indian/Kerguelen"),
        INDIAN_MALDIVES("Indian/Maldives"),
        PLT("PLT"),
        ASIA_KOLKATA("Asia/Kolkata"),
        ASIA_CALCUTTA("Asia/Calcutta"),
        IST("IST"),
        ASIA_KATMANDU("Asia/Katmandu"),
        ANTARCTICA_MAWSON("Antarctica/Mawson"),
        ASIA_ALMATY("Asia/Almaty"),
        ASIA_COLOMBO("Asia/Colombo"),
        ASIA_DACCA("Asia/Dacca"),
        ASIA_DHAKA("Asia/Dhaka"),
        ASIA_NOVOSIBIRSK("Asia/Novosibirsk"),
        ASIA_THIMBU("Asia/Thimbu"),
        ASIA_THIMPHU("Asia/Thimphu"),
        BST("BST"),
        ASIA_RANGOON("Asia/Rangoon"),
        INDIAN_COCOS("Indian/Cocos"),
        ASIA_BANGKOK("Asia/Bangkok"),
        ASIA_JAKARTA("Asia/Jakarta"),
        ASIA_KRASNOYARSK("Asia/Krasnoyarsk"),
        ASIA_PHNOM_PENH("Asia/Phnom_Penh"),
        ASIA_SAIGON("Asia/Saigon"),
        ASIA_VIENTIANE("Asia/Vientiane"),
        INDIAN_CHRISTMAS("Indian/Christmas"),
        VST("VST"),
        ANTARCTICA_CASEY("Antarctica/Casey"),
        ASIA_BRUNEI("Asia/Brunei"),
        ASIA_HONG_KONG("Asia/Hong_Kong"),
        ASIA_IRKUTSK("Asia/Irkutsk"),
        ASIA_KUALA_LUMPUR("Asia/Kuala_Lumpur"),
        ASIA_MACAO("Asia/Macao"),
        ASIA_MANILA("Asia/Manila"),
        ASIA_SHANGHAI("Asia/Shanghai"),
        ASIA_SINGAPORE("Asia/Singapore"),
        ASIA_TAIPEI("Asia/Taipei"),
        ASIA_UJUNG_PANDANG("Asia/Ujung_Pandang"),
        ASIA_ULAANBAATAR("Asia/Ulaanbaatar"),
        ASIA_ULAN_BATOR("Asia/Ulan_Bator"),
        AUSTRALIA_PERTH("Australia/Perth"),
        CTT("CTT"),
        ASIA_JAYAPURA("Asia/Jayapura"),
        ASIA_PYONGYANG("Asia/Pyongyang"),
        ASIA_SEOUL("Asia/Seoul"),
        ASIA_TOKYO("Asia/Tokyo"),
        ASIA_YAKUTSK("Asia/Yakutsk"),
        JST("JST"),
        PACIFIC_PALAU("Pacific/Palau"),
        ACT("ACT"),
        AUSTRALIA_ADELAIDE("Australia/Adelaide"),
        AUSTRALIA_BROKEN_HILL("Australia/Broken_Hill"),
        AUSTRALIA_DARWIN("Australia/Darwin"),
        AET("AET"),
        ANTARCTICA_DUMONTDURVILLE("Antarctica/DumontDUrville"),
        ASIA_VLADIVOSTOK("Asia/Vladivostok"),
        AUSTRALIA_BRISBANE("Australia/Brisbane"),
        AUSTRALIA_HOBART("Australia/Hobart"),
        AUSTRALIA_SYDNEY("Australia/Sydney"),
        PACIFIC_GUAM("Pacific/Guam"),
        PACIFIC_PORT_MORESBY("Pacific/Port_Moresby"),
        PACIFIC_SAIPAN("Pacific/Saipan"),
        PACIFIC_TRUK("Pacific/Truk"),
        AUSTRALIA_LORD_HOWE("Australia/Lord_Howe"),
        ASIA_MAGADAN("Asia/Magadan"),
        PACIFIC_EFATE("Pacific/Efate"),
        PACIFIC_GUADALCANAL("Pacific/Guadalcanal"),
        PACIFIC_KOSRAE("Pacific/Kosrae"),
        PACIFIC_NOUMEA("Pacific/Noumea"),
        PACIFIC_PONAPE("Pacific/Ponape"),
        SST("SST"),
        PACIFIC_NORFOLK("Pacific/Norfolk"),
        ANTARCTICA_MCMURDO("Antarctica/McMurdo"),
        ASIA_ANADYR("Asia/Anadyr"),
        ASIA_KAMCHATKA("Asia/Kamchatka"),
        NST("NST"),
        PACIFIC_AUCKLAND("Pacific/Auckland"),
        PACIFIC_FIJI("Pacific/Fiji"),
        PACIFIC_FUNAFUTI("Pacific/Funafuti"),
        PACIFIC_MAJURO("Pacific/Majuro"),
        PACIFIC_NAURU("Pacific/Nauru"),
        PACIFIC_TARAWA("Pacific/Tarawa"),
        PACIFIC_WAKE("Pacific/Wake"),
        PACIFIC_WALLIS("Pacific/Wallis"),
        PACIFIC_CHATHAM("Pacific/Chatham"),
        PACIFIC_ENDERBURY("Pacific/Enderbury"),
        PACIFIC_TONGATAPU("Pacific/Tongatapu"),
        PACIFIC_KIRITIMATI("Pacific/Kiritimati");

        private String zoneId;
        private static final Map<String, TimeZoneId> valueToEnumMapping = Collections.unmodifiableMap(initializeMapping());

        private static Map<String, TimeZoneId> initializeMapping() {
            Map<String, TimeZoneId> valueToEnumMapping = new HashMap<String, TimeZoneId>();
            for (TimeZoneId timeZoneId : TimeZoneId.values()) {
                valueToEnumMapping.put(timeZoneId.getZoneId(),timeZoneId);
            }
            return valueToEnumMapping;
        }


        TimeZoneId(String zoneId) {
            this.zoneId = zoneId;
        }

        public String getZoneId() {
            return zoneId;
        }

        public static TimeZoneId getTimeZoneIdByValue(String value) {

            if(!valueToEnumMapping.containsKey(value)) {
                throw new IllegalArgumentException("Error in parsing TimeZoneId value : "+value);
            }

            return valueToEnumMapping.get(value);
        }
    }

    public static Date stringToDate(String date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            LOG.debug(e.getMessage());
        }
        return null;
    }

    public static String dateToString(Date date, String pattern) {
        if (date != null && StringUtils.hasText(pattern)) {
            DateFormat format = new SimpleDateFormat(pattern);
            return format.format(date);
        } else {
            return null;
        }
    }

    public static boolean between(Date dateTime, DateRange dateRange) {
        Calendar givenTime = Calendar.getInstance();
        givenTime.setTime(dateTime);
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(dateRange.getStart());
        if (givenTime.after(startTime)) {
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(dateRange.getEnd());
            return givenTime.before(endTime);
        }
        return false;
    }

    public static boolean isPastTime(Date input) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(getCurrentTime());
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTime(input);
        return inputTime.before(currentTime);
    }

    public static boolean isFutureTime(Date input) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(getCurrentTime());
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTime(input);
        return inputTime.after(currentTime);
    }

    public static boolean isFirstDayOfMonth(Date input) {
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTime(input);
        return inputTime.get(Calendar.DATE) == 1;
    }

    public static boolean isFirstDayOfFinancialYear(Date input) {
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTime(input);
        return inputTime.get(Calendar.DATE) == 1 && inputTime.get(Calendar.MONTH) == 3;
    }

    public static Date clearTime(Date dateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date clearDate(Date dateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.clear(Calendar.YEAR);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getCurrentTime() {
        return new Date();
    }

    public static Date getDateFromEpoch(long epoch) {
        return new Date(epoch);
    }

    public static String getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return getWeekDayName(c.get(Calendar.DAY_OF_WEEK) - 1);
    }

    public static String getDayOfWeekForToday() {
        Calendar c = Calendar.getInstance();
        return getWeekDayName(c.get(Calendar.DAY_OF_WEEK) - 1);
    }

    public static Date getCurrentDate() {
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    public static boolean isSameDay(Date d1, Date d2) {
        return org.apache.commons.lang3.time.DateUtils.isSameDay(d1,d2);
    }

    public static boolean isToday(Date date) {
        return isSameDay(getCurrentTime(), date);
    }

    /**
     * Usage Example : For date 5 Feb 2011 enter year = 2011, month = 2, and day = 5
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date createDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day, 0, 0, 0);
        return date.getTime();
    }

    public static Date addToDate(Date date, int type, int noOfUnits) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, noOfUnits);
        return calendar.getTime();
    }

    /**
     * Limit a date's resolution. For example, the date <code>2004-09-21 13:50:11</code> will be changed to
     * <code>2004-09-01 00:00:00</code> when using <code>Resolution.MONTH</code>.
     *
     * @param resolution The desired resolution of the date to be returned
     * @return the date with all values more precise than <code>resolution</code> set to 0 or 1
     */
    public static Date round(Date date, Resolution resolution) {
        return new Date(round(date.getTime(), resolution));
    }

    public static long round(long time, Resolution resolution) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date(time));

        if (resolution == Resolution.YEAR) {
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.MONTH) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.WEEK) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.QUARTER) {
            int quarter = (cal.get(Calendar.MONTH) / 3);
            switch (quarter) {
                case 3:
                    cal.set(Calendar.MONTH, Calendar.OCTOBER);
                    break;
                case 0:
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                    break;
                case 1:
                    cal.set(Calendar.MONTH, Calendar.APRIL);
                    break;
                case 2:
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    break;
            }
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.DAY) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.HOUR) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.MINUTE) {
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.SECOND) {
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.MILLISECOND) {
            // don't cut off anything
        } else {
            throw new IllegalArgumentException("unknown resolution " + resolution);
        }
        return cal.getTime().getTime();
    }

    /**
     * Specifies the time granularity.
     */
    public enum Resolution {
        YEAR(946080000000L),
        QUARTER(7776000000L),
        MONTH(2592000000L),
        WEEK(7 * 24 * 60 * 60 * 1000),
        DAY(24 * 60 * 60 * 1000),
        HOUR(60 * 60 * 1000),
        MINUTE(60 * 1000),
        SECOND(1000),
        MILLISECOND(1);

        private final long milliseconds;

        private Resolution(long milliseconds) {
            this.milliseconds = milliseconds;
        }

        public long milliseconds() {
            return milliseconds;
        }
    }

    public static final class Interval {
        private final TimeUnit timeUnit;
        private final long     period;

        public Interval(TimeUnit timeUnit, long period) {
            this.timeUnit = timeUnit;
            this.period = period;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public long getPeriod() {
            return period;
        }

        public int toMinutes() {
            return (int) timeUnit.toMinutes(period);
        }

    }

    public static DateRange getFutureInterval(Date startTime, Interval interval) {
        Date endTime = addToDate(startTime, Calendar.MINUTE, interval.toMinutes());
        return new DateRange(startTime, endTime);
    }

    public static DateRange getPastInterval(Date endTime, Interval interval) {
        Date startTime = addToDate(endTime, Calendar.MINUTE, -interval.toMinutes());
        return new DateRange(startTime, endTime);
    }

    public static DateRange getDayRange(Date anytime) {
        Date startTime = round(anytime, Resolution.DAY);
        return new DateRange(startTime, DateUtils.addToDate(startTime, Calendar.DATE, 1));
    }

    public static DateRange getLastDayRange() {
        return getDayRange(DateUtils.addToDate(DateUtils.getCurrentTime(), Calendar.DATE, -1));
    }

    public static DateRange getDaysRange(Date anytime, int days) {
        Date startTime = round(anytime, Resolution.DAY);
        return new DateRange(startTime, DateUtils.addToDate(startTime, Calendar.DATE, days));
    }

    public static DateRange getLastDaysRange(int pastDays) {
        return getDaysRange(DateUtils.addToDate(DateUtils.getCurrentTime(), Calendar.DATE, -pastDays), pastDays);
    }

    public static String getWeekDayName(int day) {
        String[] namesOfDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        if (day < namesOfDays.length) {
            return namesOfDays[day];
        }
        return null;
    }

    public static String getMonthName(int month) {
        String[] monthNames = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
        if (month < monthNames.length) {
            return monthNames[month];
        }
        return null;
    }

    public static class DateRange {

        private Date                start;

        private Date                end;

        private DateUtils.TextRange textRange;

        private Resolution          resolution;

        public DateRange() {
        }

        public DateRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        public DateRange(DateUtils.TextRange textRange) {
            setTextRange(textRange);
        }

        public DateRange(DateUtils.TextRange textRange, Resolution resolution) {
            this.resolution = resolution;
            setTextRange(textRange);
        }

        public Date getStart() {
            if (textRange != null) {
                setTextRange(textRange);
            }
            return start;
        }

        public Date getEnd() {
            if (textRange != null) {
                setTextRange(textRange);
            }
            return end;
        }

        public void setResolution(Resolution resolution) {
            this.resolution = resolution;
            setTextRange(textRange);
        }

        public void setStart(Date start) {
            this.start = start;
        }

        public void setEnd(Date end) {
            this.end = end;
        }

        public int getDuration(Resolution resolution) {
            return getStart() != null && getEnd() != null ? diff(getStart(), getEnd(), resolution) : 0;
        }

        @Override
        public String toString() {
            return getStart() + " - " + getEnd() + "-" + (textRange == null ? "" : textRange);
        }

        public DateUtils.TextRange getTextRange() {
            return textRange;
        }

        public void setTextRange(DateUtils.TextRange textRange) {
            if (textRange != null) {
                switch (textRange) {
                    case TODAY:
                        start = round(getCurrentTime(), Resolution.DAY);
                        end = addToDate(start, Calendar.DATE, 1);
                        break;
                    case YESTERDAY:
                        end = round(getCurrentTime(), Resolution.DAY);
                        start = addToDate(end, Calendar.DATE, -1);
                        break;
                    case LAST_WEEK:
                        end = round(getCurrentTime(), Resolution.WEEK);
                        start = addToDate(end, Calendar.DATE, -7);
                        break;
                    case LAST_MONTH:
                        end = round(getCurrentTime(), Resolution.MONTH);
                        start = addToDate(end, Calendar.MONTH, -1);
                        break;
                    case THIS_MONTH:
                        start = round(getCurrentTime(), Resolution.MONTH);
                        end = addToDate(start, Calendar.MONTH, 1);
                        break;
                    case LAST_7_DAYS:
                        if (resolution != null) {
                            end = round(getCurrentTime(), resolution);
                        } else {
                            end = round(getCurrentTime(), Resolution.MILLISECOND);
                        }
                        start = addToDate(end, Calendar.DATE, -7);
                        break;
                    case LAST_30_DAYS:
                        if (resolution != null) {
                            end = round(getCurrentTime(), resolution);
                        } else {
                            end = round(getCurrentTime(), Resolution.MILLISECOND);
                        }
                        start = addToDate(end, Calendar.DATE, -30);
                        break;
                    case LAST_60_DAYS:
                        if (resolution != null) {
                            end = round(getCurrentTime(), resolution);
                        } else {
                            end = round(getCurrentTime(), Resolution.MILLISECOND);
                        }
                        start = addToDate(end, Calendar.DATE, -60);
                        break;
                    case LAST_90_DAYS:
                        if (resolution != null) {
                            end = round(getCurrentTime(), resolution);
                        } else {
                            end = round(getCurrentTime(), Resolution.MILLISECOND);
                        }
                        start = addToDate(end, Calendar.DATE, -90);
                        break;
                    case LAST_QUARTER:
                        end = round(getCurrentTime(), Resolution.QUARTER);
                        start = addToDate(end, Calendar.MONTH, -3);
                        break;
                    case THIS_QUARTER:
                        start = round(getCurrentTime(), Resolution.QUARTER);
                        end = addToDate(start, Calendar.MONTH, 3);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid value for textRange");
                }
            }
            this.textRange = textRange;
        }

        public boolean isDateBeforeOrWithInRange(Date date) {
            return date.after(start) || date.after(end);
        }
    }

    /**
     * This method can find a time embedded in a string in the following formats : hhmm, hh:mm, h:m, h:mm, hh:m (1 space
     * after/before : is also accepted) This method runs faster than the parse() method of Java
     *
     * @param token
     * @return the date object
     */
    public static Date parseTime(String token) {
        if (token == null || "".equals(token)) {
            return null;
        }

        Calendar cal = new GregorianCalendar();
        cal.clear();

        char[] ctoken = token.toCharArray();
        StringBuilder hours = new StringBuilder(2);
        StringBuilder mins = new StringBuilder(2);

        if (token.indexOf(":") < 0) {
            for (int i = 0; i < ctoken.length; i++) {
                if (Character.isDigit(ctoken[i]) && (i + 1 < ctoken.length && Character.isDigit(ctoken[i + 1]))) {
                    if ((i + 2 < ctoken.length && Character.isDigit(ctoken[i + 2])) && (i + 3 < ctoken.length && Character.isDigit(ctoken[i + 3]))) {
                        hours.append(ctoken[i]).append(ctoken[i + 1]);
                        mins.append(ctoken[i + 2]).append(ctoken[i + 3]);
                    }
                }
            }
        } else {
            for (int i = 0; i < ctoken.length; i++) {
                if (ctoken[i] == ':') {
                    if (i - 1 >= 0 && Character.isDigit(ctoken[i - 1])) {
                        if (i - 2 >= 0 && Character.isDigit(ctoken[i - 2])) {
                            hours.append(ctoken[i - 2]).append(ctoken[i - 1]);
                        } else {
                            hours.append(ctoken[i - 1]);
                        }
                    } else {
                        if (i - 2 >= 0 && Character.isDigit(ctoken[i - 2])) {
                            if (i - 3 >= 0 && Character.isDigit(ctoken[i - 3])) {
                                hours.append(ctoken[i - 3]).append(ctoken[i - 2]);
                            } else {
                                hours.append(ctoken[i - 2]);
                            }
                        }
                    }

                    if (i + 1 < ctoken.length && Character.isDigit(ctoken[i + 1])) {
                        if (i + 2 < ctoken.length && Character.isDigit(ctoken[i + 2])) {
                            mins.append(ctoken[i + 1]).append(ctoken[i + 2]);
                        } else {
                            mins.append(ctoken[i + 1]);
                        }
                    } else {
                        if (i + 2 < ctoken.length && Character.isDigit(ctoken[i + 2])) {
                            if (i + 3 < ctoken.length && Character.isDigit(ctoken[i + 3])) {
                                mins.append(ctoken[i + 2]).append(ctoken[i + 3]);
                            } else {
                                mins.append(ctoken[i + 2]);
                            }
                        }
                    }
                    break;
                }
            }
        }
        try {
            int hrs = Integer.parseInt(hours.toString());
            int minutes = Integer.parseInt(mins.toString());
            if ((token.contains("pm") || token.contains("PM")) && hrs != 12) {
                hrs += 12;
                hrs %= 24;
            }
            if ((token.contains("am") || token.contains("AM")) && hrs == 12) {
                hrs = 0;
            }

            cal.set(Calendar.HOUR_OF_DAY, hrs);
            cal.set(Calendar.MINUTE, minutes);
        } catch (NumberFormatException e) {
            LOG.debug("Could not parse " + hours + ":" + mins + " as time. Initial token was " + token);
            return null;
        }

        return cal.getTime();
    }

    public static Date addTimeOfDate(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        calendar1.add(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
        calendar1.add(Calendar.SECOND, calendar2.get(Calendar.SECOND));
        calendar1.add(Calendar.MILLISECOND, calendar2.get(Calendar.MILLISECOND));
        return calendar1.getTime();
    }

    public static int diff(Date date1, Date date2, Resolution resolution) {
        long diff = Math.abs(date1.getTime() - date2.getTime());
        return Math.round(diff / resolution.milliseconds());
    }

    public static int getCurrentHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * @return
     */
    public static Date getCurrentDayTime() {
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.clear(Calendar.YEAR);
        return cal.getTime();
    }

    public static boolean isItAGoodTimeToRun(String cronExpression, int delayForNextRun) {
        Date currentTime = DateUtils.getCurrentTime();
        Long nextRun = getNextRun(cronExpression, currentTime).getTime();
        if ((nextRun > currentTime.getTime()) && (nextRun <= (currentTime.getTime() + delayForNextRun * 1000))) {
            return true;
        }
        return false;
    }

    public static Date getNextRun(String cronExpression) {
        return getNextRun(cronExpression, DateUtils.getCurrentTime());
    }

    public static Date getNextRun(String cronExpression, Date currentTime) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
        return cronSequenceGenerator.next(currentTime);
    }

    public static void main(String[] args) throws  IOException, InterruptedException {
        //        System.out.println(new BigDecimal("0.00"));

        //        Process p = Runtime.getRuntime().exec("/bin/bash -c wget --quiet   --output-document   - 'https://www.singlekart.com/wp-json/wc/v1/products?consumer_key=ck_ac07ede27ebf2884d9a06dada0fb1eb3a6f19810&consumer_secret=cc9ec0eeb500af6b34bcec948ac6ee88396daf4cc&per_page=1&page=1'");
        //        p.waitFor();
        ////        System.out.println(p.getOutputStream());
        //        String s = null;
        //        try {
        //            BufferedReader br = new BufferedReader(
        //                    new InputStreamReader(p.getInputStream()));
        //            while ((s = br.readLine()) != null) {
        //                System.out.println(s);
        //            }
        //        } catch (IOException ioe) {
        //            ioe.printStackTrace();
        //        }
        //        System.out.println(new HttpSender(true).executeGet("https://www.singlekart.com/wp-json/wc/v1/products?consumer_key=ck_ac07ede27ebf2884d9a06dada0fb1eb3a6f19810&consumer_secret=cs_c9ec0eeb500af6b34bcec948ac6ee88396daf4cc&per_page=1&page=1",null));
//        System.out.println("singlekart:" + new HttpSender(false).executeGet("https://www.singlekart.com", null));
//        System.out.println("varkalasilksarees:" + new HttpSender(true).executeGet("http://varkalasilksarees.com/", null));

        //        System.out.println(dateToString(addToDate(getCurrentDate(), Calendar.DATE, -30),"yyyy-MM-ddTmm:HH:ssZ"));
    }

    public static Date addDaysToDate(Date date, Integer daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, daysToAdd);
        return calendar.getTime();
    }

    public static Date convertDateFromTimeZoneToDefault(Date date, String sourceTimeZone) {
        return convertDateFromTimeZone(date,sourceTimeZone,ZoneId.systemDefault().getId());
    }

    public static Date convertDateFromTimeZone(Date date, String sourceTimeZone,String targetTimeZone) {
        Date convertedDate = null;

        if(date!=null) {
            LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of(sourceTimeZone));
            convertedDate =  Date.from(ldt.atZone(ZoneId.of(targetTimeZone)).toInstant());
        }

        return convertedDate;
    }

    public static boolean isValidDateFormat(String date, String format) {
        LocalDateTime ldt = null;
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
        try {
            ldt = LocalDateTime.parse(date, fomatter);
            String result = ldt.format(fomatter);
            return result.equals(date);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(date, fomatter);
                String result = ld.format(fomatter);
                return result.equals(date);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(date, fomatter);
                    String result = lt.format(fomatter);
                    return result.equals(date);
                } catch (DateTimeParseException e2) {
                    LOG.debug(e2.getMessage(), e2);
                }
            }
        }
        return false;
    }

}
