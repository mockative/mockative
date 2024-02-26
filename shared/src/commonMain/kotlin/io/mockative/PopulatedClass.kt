package io.mockative

@MockativeMockable
class PopulatedClass(
    private val class2: Class2,
    private val inner2: InnerClass,
    internal val internalVal: String,
    protected val protectedVal: String,
    val class3: Class3,
    val number: Int?,
    val map: Map<String, String>?,
    val list: List<String>,
    val sequence: List<List<List<String>>>,
    val charSequence: CharSequence,
    val string: String,
    val bArray: BooleanArray,
    val inner: InnerClass,
    val arrayList: ArrayList<String>,
    val deque: ArrayDeque<String>,
    val linkedMap: LinkedHashMap<String, String>,
    val hashMap: HashMap<String, String>,
    val linkedSet: LinkedHashSet<String>,
    val hashSet: HashSet<String>,
) {

    @MockativeMockable
    class InnerClass(val s: String) {
        fun wassup() = "Hello"
    }
    val notConstructorParam = "notConstructorParam"

    fun greet() = "Hello"
}

@MockativeMockable
class Class2(val class3: Class3) {
    val message1 = "message1"
    val message2 = "message2"
}

@MockativeMockable
class Class3 {
    val message1 = "message1"
    val message2 = "message2"
}
