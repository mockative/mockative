package io.mockative

import java.io.Writer

class MockWriter(private val writer: Writer, private val stubsUnitByDefault: Boolean) {
    fun appendMock(mock: MockDescriptor) {
        appendPackage(mock)
        writer.appendLine()
        writer.appendLine()

        appendDeclaration(mock)
        appendGenericConstraints(mock)

        writer.append(" {")
        writer.appendLine()

        appendProperties(mock)
        appendSeparator(mock)
        appendFunctions(mock)

        writer.append('}')
    }

    private fun appendGenericConstraints(mock: MockDescriptor) {
        val genericConstraints = mock.typeParameters
            .flatMap { typeParameter -> typeParameter.bounds.map { bound -> typeParameter to bound } }
            .joinToString(", ") { (typeParameter, bound) -> "${typeParameter.name} : $bound" }

        if (genericConstraints.isNotEmpty()) {
            writer.append(" where ")
            writer.append(genericConstraints)
        }
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

        val typeParameters = mock.typeParameters
        if (typeParameters.isNotEmpty()) {
            writer.append('<')
            writer.append(typeParameters.joinToString(", ") { it.name })
            writer.append('>')
        }

        writer.append(" : ")
        writer.append(MockativeTypes.Mockable.name)
        writer.append("(stubsUnitByDefault = $stubsUnitByDefault), ")
        writer.append(mock.qualifiedName)

        if (typeParameters.isNotEmpty()) {
            writer.append('<')
            writer.append(typeParameters.joinToString(", ") { it.name })
            writer.append('>')
        }
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