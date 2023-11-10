package com.example.cupcake.test

import androidx.navigation.NavController
import org.junit.Assert.assertEquals

/**
 * Verificar si la ruta pasada es la ruta actual
 *
 * @param expectedRouteName es el nombre de la ruta que queremos verificar, si es la ruta actual
 *
 * @author sandres@gmail.com
 */
fun NavController.assertCurrentRouteName(expectedRouteName: String): Unit{
    assertEquals(expectedRouteName, this.currentBackStackEntry?.destination?.route)
}