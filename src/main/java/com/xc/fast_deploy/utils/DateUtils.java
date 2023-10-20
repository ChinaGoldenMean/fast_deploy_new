package com.xc.fast_deploy.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

public class DateUtils {
  
  /**
   * 取时间戳并配合uuid中间四位做成唯一标识
   *
   * @return
   */
  public static String generateDateString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    StringBuilder sb = new StringBuilder();
    sb.append(dateFormat.format(new Date())).append("_").append(UUID.randomUUID().toString().split("-")[1]);
    return sb.toString();
  }
  
  public static String generateDateOnlyString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    StringBuilder sb = new StringBuilder();
    sb.append(dateFormat.format(new Date()));
    return sb.toString();
  }
  
  public static Date offsetDateTimeToDate(OffsetDateTime dateTime) {
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDate localDate = dateTime.toLocalDate().now();
    ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
    
    return Date.from(zdt.toInstant());
  }
}

