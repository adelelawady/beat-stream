package com.konsol.beatstream.service.scheduler;

import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.domain.enumeration.ReferenceType;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.service.TaskNodeService;
import com.konsol.beatstream.service.audioPlugins.youtube.YouTubePlaylistInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TaskNodeTester implements CommandLineRunner {

    @Autowired
    TaskNodeService taskNodeService;

    @Autowired
    private TaskNodeRepository taskNodeRepository;

    /**
     * @param args incoming main method arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        /*
        TaskNode task=taskNodeService.createTask("[PLAYLIST]", ReferenceType.YOUTUBE,"PUUvj9CxUYU","671ae52319065d658022c391",null, DownloadType.AUDIO_PLAYLIST,0);


        TaskNode taskNodeChild= taskNodeService.createTask("_GwPYLW-PWY", ReferenceType.YOUTUBE,"_GwPYLW-PWY",task.getPlaylistId(),
            null, DownloadType.AUDIO,0);
        task.getChildTasks().add(taskNodeChild);
        taskNodeRepository.save(task);


        TaskNode taskNodeChild1= taskNodeService.createTask("9lfH7vV2YLQ", ReferenceType.YOUTUBE,"9lfH7vV2YLQ",task.getPlaylistId(),
            null, DownloadType.AUDIO,0);
        task.getChildTasks().add(taskNodeChild1);
        taskNodeRepository.save(task);


        taskNodeChild1.setParentTask(task);
        taskNodeChild.setParentTask(task);
        taskNodeRepository.save(taskNodeChild1);
        taskNodeRepository.save(taskNodeChild);


         */

    }
}
