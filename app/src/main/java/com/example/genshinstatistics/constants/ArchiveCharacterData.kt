package com.example.genshinstatistics.constants

import com.example.genshinstatistics.R
import com.example.genshinstatistics.enum.ItemType
import com.example.genshinstatistics.model.Character
import com.example.genshinstatistics.model.Region

object ArchiveCharacterData {
    val ITEMS: List<Character> by lazy {
        listOf(
            // 5-Star Characters
            Character(
                id = 1,
                name = "Albedo",
                region = Region.MONDSTADT,
                iconBgColor = R.drawable.ic_geo_bg,
                birthdate = "09/13",
                element = R.drawable.ic_geo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 2,
                name = "Alhaitham",
                region = Region.SUMERU,
                iconBgColor = R.drawable.ic_dendro_bg,
                birthdate = "09/26",
                element = R.drawable.ic_element_dendro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 3,
                name = "Aloy",
                region = Region.UNKNOWN,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "02/06",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 4,
                name = "Arataki Itto",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_geo_bg,
                birthdate = "06/01",
                element = R.drawable.ic_geo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 5,
                name = "Arlecchino",
                region = Region.SNEZHNAYA,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "08/22",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 6,
                name = "Baizhu",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_dendro_bg,
                birthdate = "04/25",
                element = R.drawable.ic_element_dendro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 7,
                name = "Chasca",
                region = Region.NATLAN,
                iconBgColor = R.drawable.ic_anemo_bg,
                birthdate = "12/10",
                element = R.drawable.ic_anemo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 8,
                name = "Chiori",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_geo_bg,
                birthdate = "08/17",
                element = R.drawable.ic_element_geo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 9,
                name = "Citlali",
                region = Region.NATLAN,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "01/20",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 10,
                name = "Clorinde",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_electro_bg,
                birthdate = "09/20",
                element = R.drawable.ic_element_electro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 11,
                name = "Cyno",
                region = Region.SUMERU,
                iconBgColor = R.drawable.ic_electro_bg,
                birthdate = "07/23",
                element = R.drawable.ic_element_electro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 12,
                name = "Dehya",
                region = Region.SUMERU,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "04/25",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 13,
                name = "Diluc",
                region = Region.MONDSTADT,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "04/30",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 14,
                name = "Emilie",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_dendro_bg,
                birthdate = "09/28",
                element = R.drawable.ic_element_dendro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 15,
                name = "Eula",
                region = Region.MONDSTADT,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "10/25",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 16,
                name = "Furina",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "10/13",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 17,
                name = "Ganyu",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "12/02",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 18,
                name = "Hu Tao",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "07/15",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 19,
                name = "Jean",
                region = Region.MONDSTADT,
                iconBgColor = R.drawable.ic_anemo_bg,
                birthdate = "03/14",
                element = R.drawable.ic_anemo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 20,
                name = "Kaedehara Kazuha",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_anemo_bg,
                birthdate = "10/29",
                element = R.drawable.ic_anemo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 21,
                name = "Kamisato Ayaka",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "09/28",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 22,
                name = "Kamisato Ayato",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "03/26",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 23,
                name = "Keqing",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_electro_bg,
                birthdate = "11/20",
                element = R.drawable.ic_element_electro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 24,
                name = "Kinich",
                region = Region.NATLAN,
                iconBgColor = R.drawable.ic_dendro_bg,
                birthdate = "11/11",
                element = R.drawable.ic_element_dendro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 25,
                name = "Klee",
                region = Region.MONDSTADT,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "07/27",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 26,
                name = "Lyney",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "02/02",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 27,
                name = "Mona",
                region = Region.MONDSTADT,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "08/31",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 28,
                name = "Mualani",
                region = Region.NATLAN,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "08/03",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 29,
                name = "Nahida",
                region = Region.SUMERU,
                iconBgColor = R.drawable.ic_dendro_bg,
                birthdate = "06/27",
                element = R.drawable.ic_element_dendro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 30,
                name = "Navia",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_geo_bg,
                birthdate = "08/16",
                element = R.drawable.ic_geo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 31,
                name = "Neuvillette",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "12/18",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 32,
                name = "Nilou",
                region = Region.SUMERU,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "12/03",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 33,
                name = "Qiqi",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "03/03",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 34,
                name = "Raiden Shogun",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_electro_bg,
                birthdate = "06/26",
                element = R.drawable.ic_element_electro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 35,
                name = "Sangonomiya Kokomi",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "02/22",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 36,
                name = "Shenhe",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "03/10",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 37,
                name = "Sigewinne",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "03/30",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 38,
                name = "Tartaglia",
                region = Region.SNEZHNAYA,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "07/20",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 39,
                name = "Tighnari",
                region = Region.SUMERU,
                iconBgColor = R.drawable.ic_dendro_bg,
                birthdate = "09/21",
                element = R.drawable.ic_element_dendro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 40,
                name = "Wanderer",
                region = Region.SUMERU,
                iconBgColor = R.drawable.ic_anemo_bg,
                birthdate = "11/18",
                element = R.drawable.ic_anemo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 41,
                name = "Wriothesley",
                region = Region.FONTAINE,
                iconBgColor = R.drawable.ic_cryo_bg,
                birthdate = "11/23",
                element = R.drawable.ic_element_cryo,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 42,
                name = "Xianyun",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_anemo_bg,
                birthdate = "04/11",
                element = R.drawable.ic_anemo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 43,
                name = "Xiao",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_anemo_bg,
                birthdate = "04/17",
                element = R.drawable.ic_anemo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 44,
                name = "Xilonen",
                region = Region.NATLAN,
                iconBgColor = R.drawable.ic_geo_bg,
                birthdate = "03/13",
                element = R.drawable.ic_geo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 45,
                name = "Yae Miko",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_electro_bg,
                birthdate = "06/27",
                element = R.drawable.ic_element_electro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 46,
                name = "Yelan",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_hydro_bg,
                birthdate = "07/20",
                element = R.drawable.ic_element_hydro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 47,
                name = "Yoimiya",
                region = Region.INAZUMA,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "06/21",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 48,
                name = "Zhongli",
                region = Region.LIYUE,
                iconBgColor = R.drawable.ic_geo_bg,
                birthdate = "12/31",
                element = R.drawable.ic_geo_bg,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            ),
            Character(
                id = 49,
                name = "Mavuika",
                region = Region.NATLAN,
                iconBgColor = R.drawable.ic_pyro_bg,
                birthdate = "08/28",
                element = R.drawable.ic_element_pyro,
                rarity = 5,
                rank = 0,
                type = ItemType.CHARACTER
            )
        )
    }
}
