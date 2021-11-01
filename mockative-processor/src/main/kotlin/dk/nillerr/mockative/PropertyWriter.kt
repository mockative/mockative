package dk.nillerr.mockative

import java.io.Writer

class PropertyWriter(private val writer: Writer) {
    fun appendProperty(property: MockDescriptor.Property) {
        appendIndentation()
        appendModifiers(property)
        appendName(property)
        appendType(property)
        writer.appendLine()

        appendIndentation()
        appendIndentation()
        appendGetter(property)
        writer.appendLine()

        if (property.isMutable) {
            appendIndentation()
            appendIndentation()
            appendSetter(property)
            writer.appendLine()
        }
    }

    private fun appendIndentation() {
        writer.append("    ")
    }

    private fun appendModifiers(property: MockDescriptor.Property) {
        writer.append("override ")

        if (property.isMutable) {
            writer.append("var ")
        } else {
            writer.append("val ")
        }
    }

    private fun appendType(property: MockDescriptor.Property) {
        writer.append(": ")
        writer.append(property.type)
    }

    private fun appendGetter(property: MockDescriptor.Property) {
        writer.append("get() = mockGetter(\"")
        writer.append(property.name)
        writer.append("\")")
    }

    private fun appendSetter(property: MockDescriptor.Property) {
        writer.append("set(value) = mockSetter(\"")
        writer.append(property.name)
        writer.append("\", value)")
    }

    private fun appendName(property: MockDescriptor.Property) {
        writer.append(property.name)
    }
}