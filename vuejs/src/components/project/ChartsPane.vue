<template>
    <v-col>
        <v-card flat>
            <v-card-title>At a Glance</v-card-title>
            <v-row>
                <v-col v-for="(chart, idx) in this.chartUrls" :key="idx" class="mx-auto" min-width="500" max-width="500" max-height="350">
                    <v-list-item-content>
                        <iframe width="400" height="300" :src="chart"/>
                    </v-list-item-content>
                </v-col>
            </v-row>
        </v-card>
    </v-col>
</template>

<script>
    export default {
        name: 'ChartsPane',
        props: ['chartUrls']
    }
</script>

<style scoped>
    iframe {
        border: none;
        padding: 0;
    }
</style>