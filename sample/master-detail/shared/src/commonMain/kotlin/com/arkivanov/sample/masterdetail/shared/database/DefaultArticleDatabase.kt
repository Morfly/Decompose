package com.arkivanov.sample.masterdetail.shared.database

import com.arkivanov.sample.masterdetail.shared.database.LorenIpsumGenerator.generate
import com.arkivanov.sample.masterdetail.shared.database.LorenIpsumGenerator.generateSentence
import kotlin.random.Random

internal class DefaultArticleDatabase : ArticleDatabase {

    private val articles =
        List(50) { index ->
            ArticleEntity(
                id = index.toLong() + 1L,
                title = generate(count = Random.nextInt(3, 7), minWordLength = 3)
                    .joinToString(separator = " ", transform = String::capitalize),
                text = List(50) { generateSentence() }
                    .joinToString(separator = " ")
            )
        }

    private val articleMap = articles.associateBy(ArticleEntity::id)

    override fun getAll(): List<ArticleEntity> = articles

    override fun getById(id: Long): ArticleEntity = articleMap.getValue(id)
}
