package fr.lewon.dofus.bot.gui.custom

import java.awt.Color
import java.net.URL
import javax.swing.JFrame

open class CustomFrame(
    title: String,
    w: Int,
    h: Int,
    headerColor: Color,
    val headerHeight: Int,
    iconUrl: URL? = null,
    reduceButton: Boolean = true,
    closeButton: Boolean = true
) : JFrame(title) {

    init {
        val buttons = ArrayList<CustomHeaderButton>()
        if (reduceButton) buttons.add(CustomHeaderButton("-") { state = ICONIFIED })
        if (closeButton) buttons.add(CustomHeaderButton("x") { dispose() })
        CustomWindowBuilder.initCustomWindow(
            this, getRootPane(), contentPane, title, w, h, headerHeight,
            headerColor, buttons, iconUrl
        )
        isUndecorated = true
        defaultCloseOperation = EXIT_ON_CLOSE
    }

}