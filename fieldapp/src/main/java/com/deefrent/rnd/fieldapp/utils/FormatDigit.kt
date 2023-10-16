package com.deefrent.rnd.fieldapp.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class FormatDigit {
    companion object {
        fun roundTo(doubleValue: Double): String {
            return String.format(Locale.US, "%.2f", doubleValue)
        }

        fun formatDigits(wholeNumber: String): String {
            var number: Double = 0.0
            try {
                val formatter: DecimalFormat
                try {
                    number = wholeNumber.toDouble()
                } catch (e: NumberFormatException) {
                    return wholeNumber
                }
                if (number == 0.00) {
                    formatter = DecimalFormat("0.00")
                } else if (number == "0".toDouble()) {
                    formatter = DecimalFormat("0.##")
                } else {
                    formatter = DecimalFormat("#,###.00")
                }

                return formatter.format(number)
            } catch (e: Exception) {
                return wholeNumber
            }

        }


        fun getFormattedCurrency(): String {
            var currency: String = "KES"
            if (currency.isEmpty()) {
                return "KES"
            }
            return currency
        }

        fun getNumberFormatted(number: String): String {
            return String.format("${getFormattedCurrency()} %.2f", number.toDouble())
        }

        fun formatPercentage(number: String): String {
            return String.format("%.2f", number.toDouble())
        }

        fun splitTwoString(value: String): String {
            val splited: List<String> = value.split("\\s".toRegex())
            return splited[1]
        }

        fun convertStringToDouble(value: String): Double {
            val format: NumberFormat = NumberFormat.getInstance(Locale.US)
            val number: Number = format.parse(value) as Number
            return number.toDouble()
        }
    }
}
