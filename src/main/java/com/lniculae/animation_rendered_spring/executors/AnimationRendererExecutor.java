package com.lniculae.animation_rendered_spring.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.nio.file.Paths;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.lniculae.AnimationOutput.AnimationFileRenderer;
import com.lniculae.Helper.Result;
import com.lniculae.Helper.Result.Empty;
import com.lniculae.animation_rendered_spring.executors.TaskStatus.StatusKind;

@Component
public class AnimationRendererExecutor {

	private AsyncTaskExecutor executor;
	private TaskStatusManager taskStatusManager;

	public AnimationRendererExecutor(
		AsyncTaskExecutor executor,
		TaskStatusManager taskStatusManager
	) {
		this.executor = executor;
		this.taskStatusManager = taskStatusManager;
	}

    private class AnimationRendererTask implements Callable<Result<Empty>> {

		private AnimationFileRenderer renderer;
		private String filePath;
		private String taskId;

		public AnimationRendererTask(AnimationFileRenderer renderer, String filePath, String taskId) {
			this.renderer = renderer;
			this.filePath = filePath;
			this.taskId = taskId;
		}

		public Result<Empty> call() {
			taskStatusManager.updateStatus(taskId, new TaskStatus(StatusKind.Started));

			var result = renderer.renderToFile(filePath);
			if (!result.Ok()) {
				TaskStatus newStatus = new TaskStatus(StatusKind.Unsuccessful, result.Err().getMessage());
				taskStatusManager.updateStatus(taskId, newStatus);
				return result;
			}

			taskStatusManager.updateStatus(taskId, new TaskStatus(StatusKind.Successful));

			return result;
		}
	}

	public void executeRender(AnimationFileRenderer renderer, String fileFolder, String taskId) {
		String filePath = Paths.get(fileFolder, taskId).toString();
		executor.submit(new AnimationRendererTask(renderer, filePath, taskId));
	}

	public Result<Empty> submitRender(AnimationFileRenderer renderer, String fileFolder, String taskId) {
		String filePath = Paths.get(fileFolder, taskId).toString();
		Future<Result<Empty>> promise = executor.submit(new AnimationRendererTask(renderer, filePath, taskId));

		System.out.printf("Started processing task '%s'\n", taskId);

		try { return promise.get(); } catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();

			return new Result<>(new Error(e.getMessage()));
		}
	}

	public TaskStatus getVideoRenderStatus(String taskId) {
		return taskStatusManager.getStatus(taskId);
	}
}
