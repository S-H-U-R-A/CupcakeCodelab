package com.example.cupcake.test

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule

/**
 * On node with string id
 *
 * @param A Es el generico y debe ser un subtipo de [ComponentActivity]
 * @param id Es el id del recurso string que queremos obtener
 * @return Se retorna un composable que contenga un Texto que coincida con el texto pasado
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithStringId(
 @StringRes id: Int
): SemanticsNodeInteraction {
 return onNodeWithText( this.activity.getString(id)) //La regla de compose nos permite acceder a la actividad y poder obtener el string que pasemos
}