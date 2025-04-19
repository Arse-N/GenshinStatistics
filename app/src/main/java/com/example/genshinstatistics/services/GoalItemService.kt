package com.example.genshinstatistics.services

import android.annotation.SuppressLint
import android.content.Context
import com.example.genshinstatistics.enums.GoalItemStatus
import com.example.genshinstatistics.enums.ItemType
import com.example.genshinstatistics.model.GoalItem
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.util.BaseUtil
import com.example.genshinstatistics.util.JsonUtil

class GoalItemService(
    private var historyItems: ArrayList<HistoryItem>,
    private var goalItems: ArrayList<GoalItem>,
    var context: Context,
) {

    @SuppressLint("NewApi")
    fun createNewItem(goalItem: GoalItem) {
        val count = historyItems.count { it.name == goalItem.name }
        val lastItem: HistoryItem? = historyItems.firstOrNull { it.name == goalItem.name }
        goalItem.obtained = count
        if (goalItem.type == ItemType.CHARACTER) {
             goalItem.remaining = goalItem.goalCount - goalItem.obtained
        } else {
            goalItem.remaining = goalItem.goalCount - goalItem.obtained - 1
        }
        if (goalItem.remaining <= 0) {
            goalItem.status = GoalItemStatus.DONE
            goalItem.obtainedDate = lastItem?.winDate!!
        } else {
            goalItem.status = GoalItemStatus.TODO
        }
        addNewItem(goalItem)
    }

    fun updateItem(itemName: String) {
        val goalItem: GoalItem? = goalItems.find { it.name == itemName }
        val historyItem: HistoryItem? = historyItems.firstOrNull { it.name == itemName }
        if (goalItem != null) {
            goalItem.obtained = goalItem.obtained + 1
            goalItem.remaining = goalItem.remaining - 1
            if (goalItem.remaining <= 0) {
                goalItem.status = GoalItemStatus.DONE
                goalItem.obtainedDate = historyItem?.winDate!!
            }
            updateItem(goalItem)
        }
    }

    fun removeItem(itemName: String) {
        val goalItem: GoalItem? = goalItems.find { it.name == itemName }
        if (goalItem != null) {
            goalItem.obtained = goalItem.obtained - 1
            goalItem.remaining = goalItem.remaining + 1
            goalItem.status = GoalItemStatus.TODO
            updateItem(goalItem)
        }
    }

    private fun newGoalItem(oldGoalItem: GoalItem, status: GoalItemStatus) {
        val newGoalItem = GoalItem(BaseUtil.generateCode())
        newGoalItem.name = oldGoalItem.name
        newGoalItem.ascension = oldGoalItem.ascension
        newGoalItem.goalCount = oldGoalItem.goalCount
        newGoalItem.status = status
        newGoalItem.obtained = oldGoalItem.obtained
        newGoalItem.remaining = oldGoalItem.remaining
        addNewItem(newGoalItem)
    }

    private fun addNewItem(goalItem: GoalItem) {
        goalItems.add(goalItem)
        JsonUtil.writeToGoalJson(context, goalItems)
    }

    private fun updateItem(goalItem: GoalItem) {
        val i = goalItems.indexOf(goalItem)
        goalItems[i] = goalItem
        JsonUtil.writeToGoalJson(context, goalItems)
    }

    private fun removeItem(goalItem: GoalItem) {
        goalItems.remove(goalItem)
        JsonUtil.writeToGoalJson(context, goalItems)
    }
}
