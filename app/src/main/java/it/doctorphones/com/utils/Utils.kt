package it.doctorphones.com.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.*

object Utils {
    private val _tag = "Utils_DP"
    fun formatNumber(number: String?, format: PhoneNumberUtil.PhoneNumberFormat): String {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val formattedNumber: Phonenumber.PhoneNumber?
        val formatted: String?
        return try {
            formattedNumber = phoneUtil.parse(number, "IQ")//Locale.getDefault().country)
            formatted = phoneUtil.format(formattedNumber, format)
            formatted.replace(" ", "")
        } catch (e: NumberParseException) {
            e.printStackTrace()
            ""
        }
    }
}