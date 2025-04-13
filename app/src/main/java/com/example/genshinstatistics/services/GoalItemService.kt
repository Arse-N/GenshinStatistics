package com.example.genshinstatistics.services

import android.annotation.SuppressLint
import android.content.Context
import com.example.genshinstatistics.enums.GoalItemStatus
import com.example.genshinstatistics.model.GoalItem
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.util.BaseUtil
import com.example.genshinstatistics.util.JsonUtil

class GoalItemService(
    var historyItems: ArrayList<HistoryItem>,
    var goalItems: ArrayList<GoalItem>,
    var context: Context,
) {

    fun createNewItem(goalItem: GoalItem) {
        val count = historyItems.count { it.name == goalItem.name }
        goalItem.obtained = count
        goalItem.remaining = goalItem.goalCount - goalItem.obtained
        addNewItem(goalItem)
        if (goalItem.obtained > 0) {
            newGoalItem(goalItem, GoalItemStatus.DONE)
        }
    }

    fun updateItem(itemName: String) {
        val strictGoalItem: List<GoalItem> = goalItems.filter { it.name == itemName }
        if (strictGoalItem.isNotEmpty()) {
            val todoGoalItem = strictGoalItem.find { it.status == GoalItemStatus.TODO }
            val doneGoalItem = strictGoalItem.find { it.status == GoalItemStatus.DONE }
            if (todoGoalItem != null) {
                todoGoalItem.obtained = todoGoalItem.obtained + 1
                todoGoalItem.remaining = todoGoalItem.remaining - 1
                if (doneGoalItem != null) {
                    doneGoalItem.obtained = todoGoalItem.obtained
                    doneGoalItem.remaining = todoGoalItem.remaining
                    updateItem(doneGoalItem)
                } else {
                    newGoalItem(todoGoalItem, GoalItemStatus.DONE)
                }
                if (todoGoalItem.remaining <= 0) {
                    removeItem(todoGoalItem)
                } else {
                    updateItem(todoGoalItem)
                }
            }
        }
    }

    fun removeItem(itemName: String) {
        val strictGoalItem: List<GoalItem> = goalItems.filter { it.name == itemName }
        if (strictGoalItem.isNotEmpty()) {
            val todoGoalItem = strictGoalItem.find { it.status == GoalItemStatus.TODO }
            val doneGoalItem = strictGoalItem.find { it.status == GoalItemStatus.DONE }
            if (doneGoalItem != null) {
                doneGoalItem.obtained = doneGoalItem.obtained - 1
                doneGoalItem.remaining = doneGoalItem.remaining + 1
                if (todoGoalItem != null) {
                    todoGoalItem.obtained = todoGoalItem.obtained
                    todoGoalItem.remaining = todoGoalItem.remaining
                    updateItem(todoGoalItem)
                } else {
                    newGoalItem(doneGoalItem, GoalItemStatus.TODO)
                }
                if (doneGoalItem.obtained <= 0) {
                    removeItem(doneGoalItem)
                } else {
                    updateItem(doneGoalItem)
                }
            }
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
