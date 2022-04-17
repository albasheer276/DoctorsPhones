package it.doctorphones.com.utils

import android.annotation.SuppressLint
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object Utils {
    fun formatNumber(number: String?, format: PhoneNumberUtil.PhoneNumberFormat, withSpaces: Boolean = false): String {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val formattedNumber: Phonenumber.PhoneNumber?
        val formatted: String?
        return try {
            formattedNumber = phoneUtil.parse(number, "IQ")//Locale.getDefault().country)
            formatted = phoneUtil.format(formattedNumber, format)
            if (!withSpaces) {
                formatted.replace(" ", "")
            } else {
                formatted
            }

        } catch (e: NumberParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun isValidPhoneNumber(number: String): Boolean {
        if (number.trim().isEmpty()) {
            return true
        }
        if (number.trim().length != 11) {
            return false
        }
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val formattedNumber = phoneUtil.parse(number, "IQ")
            return phoneUtil.isValidNumberForRegion(formattedNumber, "IQ")
        } catch (e: NumberParseException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Get time ago.
     * get the time ago resullt like (3 mins ago, 2 hours ago)
     * this use TimeAgo third party, it support the english and arabic formats
     *
     * @param datetime Datetime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getTimeAgo(datetime: String?): String {
        return try {
            when {
                datetime.isNullOrEmpty() -> {
                    ""
                }
                else -> {
                    val localDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("Arabic")).parse(datetime)!!
                    val messages = TimeAgoMessages.Builder()
                        .withLocale(Locale.forLanguageTag("AR"))
                        .build()
                    TimeAgo.using(localDateTime.time, messages)
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(datetime: String?): String {
        return try {
            when {
                datetime.isNullOrEmpty() -> {
                    ""
                }
                else -> {
                    val localDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("Arabic")).parse(datetime)!!
                    val dateFormatter = SimpleDateFormat("h:mm a")

                    return dateFormatter.format(localDateTime)
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

}