package emortal.holeymoley.event

/*
object Switch : Event("Switch") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players.filter { it.gameMode == GameMode.SURVIVAL }
            .map { plr -> plr to game.players.filter { it != plr && it.gameMode == GameMode.SURVIVAL }.randomOrNull() }
            .forEach {
                if (it.second == null) return@forEach
                it.first.teleport(it.second!!.position)
            }
    }
}*/