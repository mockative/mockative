package io.mockative

import java.io.Writer

class MockWriter(private val writer: Writer) {
    fun appendMock(mock: MockDescriptor) {
        appendPackage(mock)
        writer.appendLine()
        writer.appendLine()

        appendDeclaration(mock)

        writer.append(" {")
        writer.appendLine()

        appendProperties(mock)
        appendSeparator(mock)
        appendFunctions(mock)

        writer.append('}')
    }

    private fun appendPackage(mock: MockDescriptor) {
        writer.append("package ")
        writer.append(mock.packageName)
    }

    private fun appendDeclaration(mock: MockDescriptor) {
        if (mock.visibility != null) {
            writer.append(mock.visibility)
            writer.append(' ')
        }

        writer.append("class ")
        writer.append(mock.mockName)
        writer.append(" : ")
        writer.append(MockativeTypes.Mocked.name)
        writer.append('<')
        writer.append(mock.qualifiedName)
        writer.append('>')
        writer.append("(), ")
        writer.append(mock.qualifiedName)
    }

    private fun appendProperties(mock: MockDescriptor) {
        val propertyWriter = PropertyWriter(writer)
        mock.properties.forEach { propertyWriter.appendProperty(it) }
    }

    private fun appendSeparator(mock: MockDescriptor) {
        if (mock.properties.isNotEmpty() && mock.functions.isNotEmpty()) {
            writer.appendLine()
        }
    }

    private fun appendFunctions(mock: MockDescriptor) {
        val functionWriter = FunctionWriter(writer)
        mock.functions.forEach { functionWriter.appendFunction(it) }
    }
}