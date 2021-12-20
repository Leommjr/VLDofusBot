package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.manager.VldbManager
import fr.lewon.dofus.bot.model.characters.CharacterStore
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.DofusClass
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object CharacterManager : VldbManager {

    private lateinit var characterStore: CharacterStore
    private lateinit var dataStoreFile: File

    override fun initManager() {
        dataStoreFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/user_data")
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        if (dataStoreFile.exists()) {
            characterStore = mapper.readValue(dataStoreFile)
        } else {
            characterStore = CharacterStore()
            saveUserData()
        }
    }

    private fun saveUserData() {
        with(OutputStreamWriter(FileOutputStream(dataStoreFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(characterStore))
            close()
        }
    }

    fun setCurrentCharacter(character: DofusCharacter) {
        if (characterStore.currentCharacterLogin != character.login || characterStore.currentCharacterPseudo != character.pseudo) {
            characterStore.currentCharacterLogin = character.login
            characterStore.currentCharacterPseudo = character.pseudo
            saveUserData()
        }
    }

    fun getCurrentCharacter(): DofusCharacter? {
        val login = characterStore.currentCharacterLogin ?: return null
        val pseudo = characterStore.currentCharacterPseudo ?: return null
        return getCharacter(login, pseudo)
    }

    fun getCharacter(login: String, pseudo: String): DofusCharacter? {
        return characterStore.characters.firstOrNull { it.login == login && it.pseudo == pseudo }
    }

    fun getCharacterByName(pseudo: String): DofusCharacter? {
        val matchingCharacters = characterStore.characters.filter { it.pseudo == pseudo }
        if (matchingCharacters.size != 1) {
            return null
        }
        return matchingCharacters[0]
    }

    fun getCharacters(): List<DofusCharacter> {
        return characterStore.characters.toList()
    }

    fun addCharacter(
        login: String, password: String, pseudo: String,
        dofusClass: DofusClass, spells: List<SpellCombination>
    ): DofusCharacter {
        getCharacter(login, pseudo)?.let {
            error("Character already registered : [$login, $pseudo]")
        }
        val character = DofusCharacter(login, password, pseudo, dofusClass, spells = ArrayList(spells))
        characterStore.characters.add(character)
        if (getCurrentCharacter() == null) {
            setCurrentCharacter(character)
        }
        saveUserData()
        return character
    }

    fun removeCharacter(character: DofusCharacter) {
        characterStore.characters.remove(character)
        if (getCurrentCharacter() == character) {
            characterStore.currentCharacterLogin = null
            characterStore.currentCharacterPseudo = null
        }
        saveUserData()
    }

    fun updateCharacter(
        character: DofusCharacter,
        login: String = character.login,
        password: String = character.password,
        pseudo: String = character.pseudo,
        dofusClass: DofusClass = character.dofusClass,
        spells: List<SpellCombination> = character.spells
    ) {
        val existingCharacter = getCharacter(login, pseudo)
        if (existingCharacter != null && existingCharacter != character) {
            error("Character already registered : [$login, $pseudo]")
        }
        if (getCurrentCharacter() == character) {
            characterStore.currentCharacterLogin = login
            characterStore.currentCharacterPseudo = pseudo
        }
        character.login = login
        character.password = password
        character.pseudo = pseudo
        character.dofusClass = dofusClass
        character.spells = ArrayList(spells)
        saveUserData()
    }

    fun updateParamValue(character: DofusCharacter, script: DofusBotScript, param: DofusBotScriptParameter) {
        character.scriptValues.updateParamValue(script.name, param.key, param.value)
        saveUserData()
    }

    fun getParamValue(character: DofusCharacter, script: DofusBotScript, param: DofusBotScriptParameter): String? {
        return character.scriptValues.getParamValue(script.name, param.key)
    }

}
