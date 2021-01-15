package com.annygab.minimemory.models

import com.annygab.minimemory.utils.DEFAULT_ICONS

//Constructing a list of cards, based on the board size, picking some random images
//and based on that, creating a memory card data class

class MemoryGame(
    private val boardSize: BoardSize,
    private val customImages: List<String>?
    ){

    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var numCardsFlips = 0
    private var indexOfSingleSelectedCard: Int? = null

    init {
        if (customImages == null){
            val chosenImages: List<Int> = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
            val randomizedImages: List<Int> = (chosenImages + chosenImages).shuffled()
            cards = randomizedImages.map { MemoryCard(it)  }
        }else{
            val randomizedImages = (customImages + customImages).shuffled()
            cards = randomizedImages.map { MemoryCard(it.hashCode(), it) }
        }
    }

    //Game Logic
    fun flipCard(position: Int): Boolean {
        numCardsFlips++
        val card: MemoryCard = cards[position]
        //3 cases:
        //0 cards previously flipped over: restore the cards + flip over the selected card
        //1 card previously flipped over: flip over the selected card + check if the images match
        //2 cards previously flipped over: restore the cards + flip over the selected card
        var foundMatch = false
        if(indexOfSingleSelectedCard == null){
            //0 or 2 cards previously flipped over
            restoreCards()
            indexOfSingleSelectedCard = position
        }else{
            //exactly 1 card previously flipped over
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if(cards[position1].identifier != cards[position2].identifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for(card in cards){
            if (!card.isMatched) { //if the card is not match then restore to its default state
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    //It´s only over when I flip over 2 cards
    fun getNumMoves(): Int {
        return numCardsFlips / 2
    }
}
