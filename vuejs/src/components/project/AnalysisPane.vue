<template>
    <v-list v-if="this.currentResult.length > 0" width="300" dense max-height="250" class="overflow-y-auto">
        <v-subheader class="result-title">{{title}}</v-subheader>
        <v-list-item class="result-list" dense v-for="(k, i) in this.currentAnalysis" :key="i">
            <v-list-item-content>
                <v-list-item-title align="left">{{k.text}}</v-list-item-title>
            </v-list-item-content>
            <v-list-item-content v-if="title === 'Entities'">
                <v-list-item-title align="left">({{k.type}})</v-list-item-title>
            </v-list-item-content>
            <v-list-item-content>
                <v-list-item-subtitle align="right">{{k.score.toFixed(2)}}</v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>
    </v-list>
    <v-list v-else>
        <v-subheader class="result-title">Analysis Data Not Set</v-subheader>
    </v-list>
</template>

<script>
    import {mapState} from 'vuex'

    export default {
        name: 'AnalysisPane',
        props: ['title'],
        data() {
            return {
                currentAnalysis: []
            }
        },
        computed: {
            ...mapState({
                currentResult: state => state.projects.currentResult
            })
        },
        created() {
            if(this.title === 'Key Phrases'){
                this.currentAnalysis = this.currentResult.analysis.comprehend.keyPhrases
            } else if(this.title === 'Entities') {
                this.currentAnalysis = this.currentResult.analysis.comprehend.entities
            }
        }
    }
</script>

<style scoped>
    .result-list {
        padding-top: 0;
        padding-bottom: 0;
    }

    .result-title {
        font-size: 20px;
        font-weight: bold;
    }
</style>