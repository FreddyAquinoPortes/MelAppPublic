package com.example.melapp.Backend

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Transformación visual personalizada para el número de teléfono
class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0, 10) else text.text
        val out = StringBuilder()
        for (i in trimmed.indices) {
            if (i == 0) out.append("(")
            if (i == 3) out.append(") ")
            if (i == 6) out.append("-")
            out.append(trimmed[i])
        }
        return TransformedText(AnnotatedString(out.toString()), OffsetMapping.Identity)
    }
}