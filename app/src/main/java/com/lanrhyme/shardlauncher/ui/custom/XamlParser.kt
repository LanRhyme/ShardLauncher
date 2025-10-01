package com.lanrhyme.shardlauncher.ui.custom

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

sealed class XamlNode

data class CardNode(val attributes: Map<String, String>, val children: List<XamlNode>) : XamlNode()
data class StackPanelNode(val attributes: Map<String, String>, val children: List<XamlNode>) : XamlNode()
data class TextBlockNode(val attributes: Map<String, String>) : XamlNode()
data class ButtonNode(val attributes: Map<String, String>) : XamlNode()
data class TextButtonNode(val attributes: Map<String, String>) : XamlNode()
data class HintNode(val attributes: Map<String, String>) : XamlNode()
data class ImageNode(val attributes: Map<String, String>) : XamlNode()
data class ListItemNode(val attributes: Map<String, String>) : XamlNode()
data class IconTextButtonNode(val attributes: Map<String, String>) : XamlNode()
data class IconButtonNode(val attributes: Map<String, String>) : XamlNode()
data class PathNode(val attributes: Map<String, String>) : XamlNode()
data class UnknownNode(val tagName: String, val children: List<XamlNode>) : XamlNode()


fun parseXaml(xaml: String): List<XamlNode> {
    return try {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            // The XAML content might not have a single root element.
            // We wrap it in a dummy root element to make it valid XML.
            setInput(StringReader("<root>$xaml</root>"))
        }
        
        var eventType = parser.eventType
        while (eventType != XmlPullParser.START_TAG && eventType != XmlPullParser.END_DOCUMENT) {
            eventType = parser.next()
        }

        if (eventType == XmlPullParser.END_DOCUMENT) {
            return emptyList()
        }

        readChildren(parser, parser.name)
    } catch (e: Exception) {
        Log.e("XamlParser", "Error parsing XAML", e)
        emptyList()
    }
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
    
    // Tags that are known to have no children can be processed faster.
    val selfClosingTags = setOf("TextBlock", "local:MyButton", "local:MyTextButton", 
        "local:MyHint", "local:MyImage", "local:MyListItem", "local:MyIconTextButton", "local:MyIconButton", "Path")
    val children: List<XamlNode> = if (parser.isEmptyElementTag || selfClosingTags.contains(tagName)) {
        emptyList()
    } else {
        readChildren(parser, tagName)
    }

    return when (tagName) {
        "local:MyCard" -> CardNode(attributes, children)
        "StackPanel" -> StackPanelNode(attributes, children)
        "TextBlock" -> TextBlockNode(attributes)
        "local:MyButton" -> ButtonNode(attributes)
        "local:MyTextButton" -> TextButtonNode(attributes)
        "local:MyHint" -> HintNode(attributes)
        "local:MyImage" -> ImageNode(attributes)
        "local:MyListItem" -> ListItemNode(attributes)
        "local:MyIconTextButton" -> IconTextButtonNode(attributes)
        "local:MyIconButton" -> IconButtonNode(attributes)
        "Path" -> PathNode(attributes)
        else -> {
            Log.w("XamlParser", "Unknown tag: $tagName")
            UnknownNode(tagName, children)
        }
    }
}

private fun getAttributes(parser: XmlPullParser): Map<String, String> {
    val attributes = mutableMapOf<String, String>()
    for (i in 0 until parser.attributeCount) {
        attributes[parser.getAttributeName(i)] = parser.getAttributeValue(i)
    }
    return attributes
}
