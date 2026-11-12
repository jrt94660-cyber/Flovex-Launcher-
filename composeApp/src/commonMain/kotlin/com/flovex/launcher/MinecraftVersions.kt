package com.flovex.launcher

data class MinecraftVersion(val id: String, val type: String)

expect fun fetchMinecraftVersions(onResult: (List<MinecraftVersion>?, String?) -> Unit)
