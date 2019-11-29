<template>
    <v-row>
        <v-col cols="12">
            <v-row>
                <v-card
                        v-for="project in this.projects"
                        v-bind:key="project.id"
                        class="mx-auto"
                        outlined max-height="225"
                        min-width="300"
                        max-width="300"
                        align="left"
                        :to="{name: 'project', params: {projectid: project._id, project: project}}">
                    <v-card-text>{{project.createdAt.toDateString()}}</v-card-text>
                    <v-card-title class="research-project-title">{{project.name}}</v-card-title>
                    <v-list-item-subtitle>
                        <v-card-text>
                            <span><v-icon>description</v-icon></span>
                            <span>{{project.documentsProcessed || 0}} documents</span>
                        </v-card-text>
                    </v-list-item-subtitle>
                    <v-list-item-subtitle>
                        <v-card-text class="research-project-text">
                            <span v-if="project.percentCompleted === 100" class="icon"><i class="fas fa-check"></i></span>
                            <span v-else>{{project.percentCompleted || 0}}% </span>
                            <span>Complete</span>
                        </v-card-text>
                    </v-list-item-subtitle>
                    <v-list-item-subtitle>
                        <v-card-text>
                            <v-progress-linear :value="project.percentCompleted || 0"></v-progress-linear>
                        </v-card-text>
                    </v-list-item-subtitle>
                </v-card>
                <v-card
                        class="mx-auto"
                        outlined
                        align="left"
                        min-width="300"
                        max-width="300"
                        max-height="225"
                        :to="{name: 'createProject'}">
                    <br/>
                    <br/>
                    <v-list-item-content align="center">
                        <v-list-item-subtitle>
                            <v-img src="@/assets/newproject.svg" position="center" max-width="85" max-height="85" alt="new project"/>
                            <v-card-subtitle>New Project</v-card-subtitle>
                        </v-list-item-subtitle>
                    </v-list-item-content>
                </v-card>
            </v-row>
        </v-col>
    </v-row>
</template>

<script>
    export default {
        name: 'ProjectSelctionPane',
        props: ['projects']
    }
</script>

<style scoped>
    .research-project-title {
        padding-left: 10px;
        padding-top: 0px;
        padding-bottom: 0px;
        font-weight: bold;
    }

    .research-project-text {
        padding-bottom: 0px;
    }
</style>