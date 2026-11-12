package com.flovex.launcher

expect fun loadLocalProfiles(): List<String>
expect fun saveLocalProfiles(profiles: List<String>)
