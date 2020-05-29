package me.danieluss.ubiquitous_systems2.data.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import javax.xml.bind.annotation.XmlRootElement

@JsonRootName("INVENTORY")
data class InventoryOut (
    @JsonProperty("ITEM")
    var items: List<ItemOut>
)