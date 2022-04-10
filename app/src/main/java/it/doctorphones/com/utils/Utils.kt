package it.doctorphones.com.utils

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource
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

    fun isValidPhoneNumber(number: String):Boolean{
        if(number.trim().isEmpty()){
            return true
        }
        if(number.trim().length != 11){
            return false
        }
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val formattedNumber = phoneUtil.parse(number, "IQ")
            return phoneUtil.isValidNumberForRegion(formattedNumber, "IQ")
        } catch (e: NumberParseException){
            e.printStackTrace()
            false
        }
    }

}