package it.doctorphones.com.utils

import android.annotation.SuppressLint
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.text.SimpleDateFormat
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
     * @param langTag Lang tag
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
                    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val localDateTime = parser.parse(datetime)!!
                    val nowInSeconds: Long = localDateTime.time
                    val localeByLanguageTag: Locale = Locale.forLanguageTag("AR")
                    val messages = TimeAgoMessages.Builder()
                        .withLocale(localeByLanguageTag)
                        .build()
                    TimeAgo.using(nowInSeconds, messages)
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

}