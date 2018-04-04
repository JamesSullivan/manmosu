package utils

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.MONTHS
import java.time.temporal.ChronoUnit.WEEKS
import java.time.temporal.ChronoUnit.YEARS

object RelativeTime {

  def format(date: Option[Timestamp]) = {
    date match {
      case Some(date) => relativeTime(date.toLocalDateTime())
      case _ => ""
    }
  }

  def age(birthDate: Timestamp) = birthDate.toLocalDateTime().until(LocalDateTime.now(), YEARS)

  def relativeTime(past: LocalDateTime): String = {
    val now = LocalDateTime.now()
    val years = past.until(now, YEARS)
    if (years > 1) return years + " years ago"
    if (years > 0) return "a year ago"
    val months = past.until(now, MONTHS)
    if (months > 1) return months + "  months ago"
    if (months > 0) return "a month ago"
    val weeks = past.until(now, WEEKS)
    if (weeks > 1) return weeks + " weeks ago"
    if (weeks > 0) return "a week ago"
    val days = past.until(now, DAYS)
    if (days > 1) return days + " days ago"
    if (days > 0) return "a day ago"
    val hours = past.until(now, HOURS)
    if (hours > 1) return hours + " hours ago"
    if (hours > 0) return "an hour ago"
    val minutes = past.until(now, MINUTES)
    if (minutes > 1) return minutes + " minutes ago"
    if (minutes > 0) return "a minute ago"
    return "just now"
  }

  def timeStampNow() = Timestamp.valueOf(LocalDateTime.now)

}