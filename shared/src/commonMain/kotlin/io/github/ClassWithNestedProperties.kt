package io.github

@Mockable
class ClassWithNestedProperties(
    private val prop: Level1
)

@Mockable
class Level1(
    private val prop: Level2
)

@Mockable
class Level2(
    private val prop: Level3
)

@Mockable
class Level3(
    private val prop: Int
)