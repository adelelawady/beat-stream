package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.TaskNodeTestSamples.*;
import static com.konsol.beatstream.domain.TaskNodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TaskNodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskNode.class);
        TaskNode taskNode1 = getTaskNodeSample1();
        TaskNode taskNode2 = new TaskNode();
        assertThat(taskNode1).isNotEqualTo(taskNode2);

        taskNode2.setId(taskNode1.getId());
        assertThat(taskNode1).isEqualTo(taskNode2);

        taskNode2 = getTaskNodeSample2();
        assertThat(taskNode1).isNotEqualTo(taskNode2);
    }

    @Test
    void childTasksTest() {
        TaskNode taskNode = getTaskNodeRandomSampleGenerator();
        TaskNode taskNodeBack = getTaskNodeRandomSampleGenerator();

        taskNode.addChildTasks(taskNodeBack);
        assertThat(taskNode.getChildTasks()).containsOnly(taskNodeBack);
        assertThat(taskNodeBack.getParentTask()).isEqualTo(taskNode);

        taskNode.removeChildTasks(taskNodeBack);
        assertThat(taskNode.getChildTasks()).doesNotContain(taskNodeBack);
        assertThat(taskNodeBack.getParentTask()).isNull();

        taskNode.childTasks(new HashSet<>(Set.of(taskNodeBack)));
        assertThat(taskNode.getChildTasks()).containsOnly(taskNodeBack);
        assertThat(taskNodeBack.getParentTask()).isEqualTo(taskNode);

        taskNode.setChildTasks(new HashSet<>());
        assertThat(taskNode.getChildTasks()).doesNotContain(taskNodeBack);
        assertThat(taskNodeBack.getParentTask()).isNull();
    }

    @Test
    void parentTaskTest() {
        TaskNode taskNode = getTaskNodeRandomSampleGenerator();
        TaskNode taskNodeBack = getTaskNodeRandomSampleGenerator();

        taskNode.setParentTask(taskNodeBack);
        assertThat(taskNode.getParentTask()).isEqualTo(taskNodeBack);

        taskNode.parentTask(null);
        assertThat(taskNode.getParentTask()).isNull();
    }
}
