package com.chatter.omeglechat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    /*
        - nap
            - Configure the application typography.
                - It allows to tweak the Material default text style to match our designs.
                    - This is achieved by creating TextStyle blocks for the style you need to specify
                        - For example body large, medium and small, and changing what's needed.
                    - Each TextStyle allows you to change your whole text with parameters, like fontFamily, weight, size
                     - It also the whole paragraph, with lineHeight, letterSpacing, and many more.
     */
    //val OmegleTypeograph = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        color = Color.White,
        //        color = colorScheme.onSecondary
    )
    //)
    /*
        - Implement the style, by opening the 'Text' composable, and setting the style parameter.
        - If you need to customize a specific parameter for text, for example color.
            - We can copy the style and customize what we need.
                - Similar to data class.
            - Alternatively, the text composable exposes the most common styling attributes like color, fontSize, fontFamily as parameters.
                - You can set them directly.
                - Supply a parameter directly, will override it in the style.
     */
    //Text(
    //    text = message,
    //    style = MaterialTheme.typography.bodyLarge
    //        .copy(color = Burgundy)
    //)

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */

)

