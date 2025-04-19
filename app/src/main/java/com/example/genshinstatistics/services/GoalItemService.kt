package com.example.genshinstatistics.services

import android.content.Context
import com.example.genshinstatistics.enums.GoalItemStatus
import com.example.genshinstatistics.model.GoalItem
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.util.BaseUtil
import com.example.genshinstatistics.util.JsonUtil

class GoalItemService(
    private var historyItems: ArrayList<HistoryItem>,
    private var goalItems: ArrayList<GoalItem>,
    var context: Context,
) {

    fun createNewItem(goalItem: GoalItem) {
        val count = historyItems.count { it.name == goalItem.name }
        goalItem.obtained = count
        goalItem.remaining = goalItem.goalCount - goalItem.obtained
        if (goalItem.obtained == goalItem.goalCount) {
            goalItem.status = GoalItemStatus.DONE
        } else {
            goalItem.status = GoalItemStatus.TODO
        }
        addNewItem(goalItem)
    }

    fun updateItem(itemName: String) {
        val goalItem: GoalItem? = goalItems.find { it.name == itemName }
        if (goalItem != null) {
            goalItem.obtained = goalItem.obtained + 1
            goalItem.remaining = goalItem.remaining - 1
            if (goalItem.remaining <= 0) {
                goalItem.status = GoalItemStatus.DONE
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
