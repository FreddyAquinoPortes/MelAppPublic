package com.example.melapp.Backend

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        var out = ""
        for (i in trimmed.indices) {
            if (i == 0) out += "("
            out += trimmed[i]
            if (i == 2) out += ") "
            if (i == 5) out += "-"
        }

        val phoneNumberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 6) return offset + 3
                if (offset <= 10) return offset + 4
                return 14
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset - 1
                if (offset <= 9) return offset - 3
                if (offset <= 14) return offset - 4
                return 10
            }
        }

        return TransformedText(AnnotatedString(out), phoneNumberOffsetTranslator)
    }
}