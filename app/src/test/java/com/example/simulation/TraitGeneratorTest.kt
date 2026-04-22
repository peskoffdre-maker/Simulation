package com.example.simulation

import com.example.simulation.simulation.Genome
import com.example.simulation.simulation.factories.TraitFactory
import org.junit.Test
import kotlin.random.Random

class TraitGeneratorTest {

    @Test
    fun testTraitGeneration() {

        val generator = TraitFactory()
        val customGenome = Genome(floatArrayOf(-1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,))
        val minGenes = Genome(FloatArray(19) {-1f})
        val avgGenes = Genome(FloatArray(19) {0f})
        val randomGenes = Genome(FloatArray(19) { Random.nextFloat() * if (Random.nextBoolean()) 1 else -1})
        val maxGenes = Genome(FloatArray(19) {1f})

        val minTraits = generator.generateTraitsFromGenome(minGenes)
        val customTraits = generator.generateTraitsFromGenome(customGenome)
        val avgTraits = generator.generateTraitsFromGenome(avgGenes)
        val randomTraits = generator.generateTraitsFromGenome(randomGenes)
        val maxTraits = generator.generateTraitsFromGenome(maxGenes)

        println("Genes: $minGenes\n Min traits: $minTraits\n")
        println("Genes: $customGenome\n Custom traits: $customTraits\n")
        println("Genes: $avgGenes\nAvg traits: $avgTraits\n")
        println("Genes: $maxGenes\nMax traits: $maxTraits\n")
        println("Genes: $randomGenes\nRandom traits: $randomTraits\n")

        repeat(20) {
            println(generator.generateTraitsFromGenome(Genome(FloatArray(19){Random.nextFloat() * if (Random.nextBoolean()) 1 else -1})))
        }
    }
}

