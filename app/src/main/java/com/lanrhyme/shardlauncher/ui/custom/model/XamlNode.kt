package com.lanrhyme.shardlauncher.ui.custom.model

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

sealed interface XamlNode {
    val attributes: Map<String, String>
    val children: List<XamlNode>
}

data class CardNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class StackPanelNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class TextBlockNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class ButtonNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class TextButtonNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class IconTextButtonNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class IconButtonNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class PathNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class HintNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class ImageNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class ListItemNode(override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode
data class UnknownNode(val name: String, override val attributes: Map<String, String>, override val children: List<XamlNode>) : XamlNode


fun parseXaml(xaml: String): List<XamlNode> {
    return try {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(StringReader(xaml))
        }
        readRootNodes(parser)
    } catch (e: Exception) {
        Log.e("XamlParser", "Error parsing XAML", e)
        emptyList()
    }
}

private fun readRootNodes(parser: XmlPullParser): List<XamlNode> {
    val nodes = mutableListOf<XamlNode>()
    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
            nodes.add(readNode(parser))
        }
        eventType = parser.next()
    }
    return nodes
}

private fun readChildren(parser: XmlPullParser, parentTagName: String): List<XamlNode> {
    val children = mutableListOf<XamlNode>()
    var eventType = parser.next()
    while (eventType != XmlPullParser.END_DOCUMENT && !(eventType == XmlPullParser.END_TAG && parser.name == parentTagName)) {
        if (eventType == XmlPullParser.START_TAG) {
            val childNode = readNode(parser)
            children.add(childNode)
        }
        eventType = parser.next()
    }
    return children
}

private fun readNode(parser: XmlPullParser): XamlNode {
    val tagName = parser.name
    val attributes = getAttributes(parser)

    val selfClosingTags = setOf("TextBlock", "local:MyButton", "local:MyTextButton", "local:MyHint", "local:MyImage", "local:MyListItem", "local:MyIconTextButton", "local:MyIconButton", "Path")
    val children: List<XamlNode> = if (parser.isEmptyElementTag || selfClosingTags.contains(tagName)) {
        emptyList()
    } else {
        readChildren(parser, tagName)
    }

    return when (tagName) {
        "local:MyCard" -> CardNode(attributes, children)
        "StackPanel" -> StackPanelNode(attributes, children)
        "TextBlock" -> TextBlockNode(attributes, children)
        "local:MyButton" -> ButtonNode(attributes, children)
        "local:MyTextButton" -> TextButtonNode(attributes, children)
        "local:MyIconTextButton" -> IconTextButtonNode(attributes, children)
        "local:MyIconButton" -> IconButtonNode(attributes, children)
        "Path" -> PathNode(attributes, children)
        "local:MyHint" -> HintNode(attributes, children)
        "local:MyImage" -> ImageNode(attributes, children)
        "local:MyListItem" -> ListItemNode(attributes, children)
        else -> UnknownNode(tagName, attributes, children)
    }
}

private fun getAttributes(parser: XmlPullParser): Map<String, String> {
    val attributes = mutableMapOf<String, String>()
    for (i in 0 until parser.attributeCount) {
        attributes[parser.getAttributeName(i)] = parser.getAttributeValue(i)
    }
    return attributes
}