<template>
    <v-content>
        <v-container fluid>
            <v-row>
                <v-col>
                    <div class="headline font-weight-light">
                        Spending total
                        <span v-for="[product, amount] of totalSpend" :key="product">
                            &emsp;
                            <span class="font-weight-medium">{{product}}</span> :
                            <span>{{amount}}</span>&emsp;
                        </span>
                    </div>
                </v-col>
                <!--<v-col>
                    <span v-for="[product, amount] of totalSpend" :key="product">{{product}} : {{amount}}, </span>
                </v-col>-->
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
                                                height="100"
                                                class="headline"
                                                outlined
                                        ></v-text-field>
                                    </v-col>

                                    <v-col>
                                        <v-overflow-btn
                                                        :items="spendingTypes"
                                                        label="What was that?"
                                                        v-model="spendingType"
                                                        outlined
                                                        height="100"
                                                        class="subtitle-1"
                                        ></v-overflow-btn>
                                    </v-col>

                                    <v-col>
                                        <v-textarea
                                                outlined
                                                label="Info"
                                                height="100"
                                                no-resize
                                                rows="1"
                                                v-model="spendingInfo"
                                        ></v-textarea>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col cols="3">
                                        <v-menu
                                                ref="menu"
                                                v-model="menu"
                                                :close-on-content-click="false"
                                                :return-value.sync="date"
                                                transition="scale-transition"
                                                offset-y
                                        >
                                            <template v-slot:activator="{ on }">
                                                <v-text-field
                                                        v-model="date"
                                                        label="When was that?"
                                                        prepend-icon="mdi-calendar"
                                                        readonly
                                                        v-on="on"
                                                ></v-text-field>
                                            </template>
                                            <v-date-picker v-model="date" no-title scrollable>
                                                <v-spacer></v-spacer>
                                                <v-btn text color="primary" @click="menu = false">Cancel</v-btn>
                                                <v-btn text color="primary" @click="$refs.menu.save(date)">OK</v-btn>
                                            </v-date-picker>
                                        </v-menu>
                                    </v-col>
                                    <v-col cols="2">
                                        <v-select
                                                :items="currencies"
                                                v-model="spendingCurrency"
                                                label="Currency"
                                                solo
                                        ></v-select>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-btn outlined block tile @click="saveNewSpending()" :disabled="spendingAmount === ''">Save</v-btn>
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
                                            <v-list-item-title class="headline mb-1"> <span>{{spending.currency}}</span>{{spending.amount}}</v-list-item-title>
                                            <v-list-item-subtitle>{{capitalize(spending.info)}}</v-list-item-subtitle>
                                        </v-list-item-content>
                                    </v-list-item>
                                    <div class="ml-4"><v-card-subtitle class="m-0 p-0">{{spending.date}}</v-card-subtitle></div>
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
                    { text: 'Съемное жилье' },
                    { text: 'Комунальные расходы' },
                    { text: 'Другое' },
                ],
                spendingType: '',
                spendingAmount: '',
                spendingInfo: '',
                totalSpend: new Map(),

                date: new Date().toISOString().substr(0, 10),
                menu: false,
                modal: false,
                menu2: false,

                currencies: ['$', '€', '£', '₽'],
                spendingCurrency: ''
            }
        },
        created() {
            this.loadSpendingItems()
        },
        methods: {
            loadSpendingItems() {
                let uri = '/api/data/loadSpendingItems'

                axios.get(uri, config).then(response => {
                    this.loadSpendingData(response.data)
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
                    'info': this.spendingInfo,
                    'date': this.date,
                    'currency': this.spendingCurrency
                }

                let uri = '/api/data/addNewSpendingItem'

                axios.post(uri, spendingData, config).then(response => {
                    this.loadSpendingData(response.data)
                    this.clearNewSpending()
                })
            },
            deleteSpending(spendingID) {
                let uri = '/api/data/deleteSpendingItem'

                axios.post(uri, spendingID, config).then(response => {
                    this.loadSpendingData(response.data)
                })
            },
            capitalize(string) {
                return string.charAt(0).toUpperCase() + string.slice(1);
            },
            loadSpendingData(data) {
                this.spendingItems = data['items']
                let total = data['totalSpend']

                this.totalSpend = new Map()
                Object.entries(total).forEach(([key, value]) => {
                    this.totalSpend.set(key, value)
                });
            }
        }
    }
</script>
<style scoped></style>
