package com.example.taskapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.domain.model.Task
import com.example.domain.usecase.ObserveTasksUseCase
import com.example.taskapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class TaskWidgetService : RemoteViewsService() {
    
    @Inject
    lateinit var observeTasksUseCase: ObserveTasksUseCase
    
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TaskWidgetFactory(applicationContext, observeTasksUseCase)
    }
}

class TaskWidgetFactory(
    private val context: Context,
    private val observeTasksUseCase: ObserveTasksUseCase
) : RemoteViewsService.RemoteViewsFactory {
    
    private var tasks: List<Task> = emptyList()
    
    override fun onCreate() {
        // Initialize
    }
    
    override fun onDataSetChanged() {
        tasks = runBlocking {
            observeTasksUseCase().first().take(10) // Show only first 10 tasks
        }
    }
    
    override fun onDestroy() {
        // Cleanup
    }
    
    override fun getCount(): Int = tasks.size
    
    override fun getViewAt(position: Int): RemoteViews {
        val task = tasks[position]
        val views = RemoteViews(context.packageName, R.layout.widget_task_item)
        
        // Set task title
        views.setTextViewText(R.id.widget_task_title, task.title)
        
        // Set task description
        if (task.description.isNotEmpty()) {
            views.setTextViewText(R.id.widget_task_description, task.description)
            views.setViewVisibility(R.id.widget_task_description, android.view.View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.widget_task_description, android.view.View.GONE)
        }
        
        // Set priority indicator
        val priorityColor = when (task.priority) {
            com.example.domain.model.TaskPriority.LOW -> android.graphics.Color.GREEN
            com.example.domain.model.TaskPriority.MEDIUM -> Color.parseColor("#FF9800")
            com.example.domain.model.TaskPriority.HIGH -> android.graphics.Color.RED
            com.example.domain.model.TaskPriority.URGENT -> android.graphics.Color.MAGENTA
        }
        views.setInt(R.id.widget_priority_indicator, "setBackgroundColor", priorityColor)
        
        // Set completion status
        views.setImageViewResource(
            R.id.widget_task_checkbox,
            if (task.isDone) R.drawable.ic_check_circle else R.drawable.ic_radio_button_unchecked
        )
        
        // Set up click intent
        val fillInIntent = Intent().apply {
            putExtra("task_id", task.id)
        }
        views.setOnClickFillInIntent(R.id.widget_task_item, fillInIntent)
        
        return views
    }
    
    override fun getLoadingView(): RemoteViews? = null
    
    override fun getViewTypeCount(): Int = 1
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun hasStableIds(): Boolean = true
}
