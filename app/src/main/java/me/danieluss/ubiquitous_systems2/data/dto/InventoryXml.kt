package me.danieluss.ubiquitous_systems2.data.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement
data class InventoryXml(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("ITEM")
    var items: List<ItemXml>
)