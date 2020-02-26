<template>
    <v-content>
        <v-container fluid>
            <v-row>
                <v-col>
                    <div class="headline font-weight-light">Spending total: {{totalSpend}}</div>
                </v-col>
            </v-row>

            <v-row>
                <v-col>
                    <v-expansion-panels>
                        <v-expansion-panel>
                            <v-expansion-panel-header disable-icon-rotate>
                                New Spending
                                <template v-slot:actions>
                                    <v-icon color="teal">mdi-currency-usd</v-icon>
                                </template>
                            </v-expansion-panel-header>

                            <v-expansion-panel-content>
                                <v-row>
                                    <v-col>
                                        <v-text-field
                                                label="$"
                                                placeholder="Your another Spending"
                                                v-model="spendingAmount"
                                        ></v-text-field>
                                    </v-col>

                                    <v-col>
                                        <v-overflow-btn style="margin-top: -0.2rem;"
                                                        :items="spendingTypes"
                                                        label="What was that?"
                                                        v-model="spendingType"
                                        ></v-overflow-btn>
                                    </v-col>

                                    <v-col>
                                        <v-textarea
                                                label="Info"
                                                dense
                                                no-resize
                                                rows="1"
                                                v-model="spendingInfo"
                                        ></v-textarea>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-btn outlined tile @click="saveNewSpending()" :disabled="spendingAmount === ''">Save</v-btn>
                                    </v-col>
                                </v-row>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </v-col>
            </v-row>

            <v-container>
                <v-row align="start" justify="space-around">
                    <v-col v-for="spending in spendingItems" :key="spending.id">
                        <v-card width="300" outlined>
                            <v-row>
                                <v-col cols="8">
                                    <v-list-item three-line>
                                        <v-list-item-content>
                                            <div class="overline mb-4">{{spending.type}}</div>
                                            <v-list-item-title class="headline mb-1">${{spending.amount}}</v-list-item-title>
                                            <v-list-item-subtitle>{{spending.info}}</v-list-item-subtitle>
                                        </v-list-item-content>
                                    </v-list-item>
                                </v-col>

                                <v-col cols="4" >
                                    <v-card-actions>
                                        <v-btn outlined text @click="deleteSpending(spending.id)">X</v-btn>
                                    </v-card-actions>
                                </v-col>
                            </v-row>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
        </v-container>
    </v-content>
</template>


<script>
    const config = {
        headers: {
            'Content-Type': 'application/json'
        }
    }
    import axios from 'axios'
    export default {
        data() {
            return {
                spendingItems: {},
                spendingTypes: [
                    { text: 'Транспорт' },
                    { text: 'Продукты' },
                    { text: 'Еда' },
                    { text: 'Досуг' },
                    { text: 'Другое' },
                ],
                spendingType: '',
                spendingAmount: '',
                spendingInfo: '',
                totalSpend: 0
            }
        },
        created() {
            this.loadSpendingItems()
        },
        methods: {
            loadSpendingItems() {
                let uri = '/api/data/loadSpendingItems'

                axios.get(uri, config).then(response => {
                    this.spendingItems = response.data['items']
                    this.totalSpend = response.data['totalSpend']
                    console.log(this.spendingItems)
                })
            },
            clearNewSpending() {
                this.spendingType = ''
                this.spendingInfo = ''
                this.spendingAmount = ''

            },
            saveNewSpending() {
                console.log(this.spendingType)

                let spendingData = {
                    'amount': this.spendingAmount,
                    'type': this.spendingType,
                    'info': this.spendingInfo
                }

                let uri = '/api/data/addNewSpendingItem'

                axios.post(uri, spendingData, config).then(response => {
                    console.log(response.data)
                    this.spendingItems = response.data['items']
                    this.totalSpend = response.data['totalSpend']
                    this.clearNewSpending()
                })
            },
            deleteSpending(spendingID) {
                let uri = '/api/data/deleteSpendingItem'

                axios.post(uri, spendingID, config).then(response => {
                    console.log(response.data)
                    this.spendingItems = response.data['items']
                    this.totalSpend = response.data['totalSpend']
                })
            }
        }
    }
</script>

<style scoped>
</style>


<!--<v-card outlined hover>
                        <v-card-actions>
                            <v-btn block outlined @click="newSpendingButtonClick()">
                                <v-icon dark>mdi-plus</v-icon>
                            </v-btn>
                        </v-card-actions>
                    </v-card>-->
<!--ADD NEW SPENDING CARD VERTICAL-->
<!--<v-row v-if="clickNewSpending">
    <v-col>
        <v-card width="300" outlined
        >
            <v-list-item three-line>
                <v-list-item-content>
                    <v-text-field
                            label="$"
                            placeholder="Your another Spending"
                            v-model="spendingAmount"
                    ></v-text-field>

                    <div class="overline mb-4">TYPE</div>
                    <v-overflow-btn
                            :items="spendingTypes"
                            label="What was that?"
                    ></v-overflow-btn>
                    &lt;!&ndash;
                    editable
                    item-value="text"
                    eager
                    v-model="spendingType"
                    &ndash;&gt;

                    <div class="overline mb-4">INFO</div>
                    <v-textarea
                            dense
                            no-resize
                            rows="1"
                            v-model="spendingInfo"
                    ></v-textarea>
                </v-list-item-content>
            </v-list-item>

            <v-card-actions>
                <v-btn text @click="saveNewSpending()" :disabled="spendingAmount === ''">Save</v-btn>
                &lt;!&ndash;<v-btn text>Reset</v-btn>&ndash;&gt;
                <v-divider inset/>
                <v-btn text @click="closeNewSpending()">X</v-btn>
            </v-card-actions>
        </v-card>
    </v-col>
</v-row>-->