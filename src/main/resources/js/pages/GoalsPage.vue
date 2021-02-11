<template>
  <v-content>
    <v-container fluid>

      <!--      -->
      <v-card height='100%' style="position: relative">
        <v-card-title class="justify-center">
          <span>myGOALS</span>
        </v-card-title>

        <v-card-subtitle>
          my goals
        </v-card-subtitle>


        <v-card-actions>


          <v-row>
            <v-col>

              <v-card max-width="500">
                <v-card-title>
                  <v-icon>
                    mdi-target
                  </v-icon>
                  <span>&nbsp;My Goal objective is gain $1000</span>
                </v-card-title>
                <v-divider/>

                <v-card-subtitle>
                  Types
                </v-card-subtitle>
                <v-card-actions>
                  <v-row style="padding-left: 2rem">
                    <v-chip
                        class="ma-2"
                        color="purple"
                    >
                      <span class="" style="color: white">Personal</span>
                    </v-chip>
                    <v-chip
                        class="ma-2"
                        color="red"
                    >
                      <span style="color: white">Crafting</span>
                    </v-chip>
                    <v-chip
                        class="ma-2"
                        color="green"
                    >
                      <span style="color: white">Business</span>
                    </v-chip>
                  </v-row>
                </v-card-actions>
                <v-divider/>

                <v-card-subtitle>
                  Timing
                </v-card-subtitle>
                <v-card-actions>
                  <v-row>
                    <v-col>
                      <span>Started</span><br>
                      <v-chip
                          outlined
                          class="ma-2"
                          color="primary"
                      >
                        <span>01.02.2021</span>
                      </v-chip>
                    </v-col>
                    <v-col>
                      <span>Shall finished</span><br>
                      <v-chip
                          outlined
                          class="ma-2"
                          color="success"
                      >
                        <span>01.03.2021</span>
                      </v-chip>
                    </v-col>
                  </v-row>
                  <!--                  <v-combobox
                                        v-model="dates"
                                        multiple
                                        chips
                                        small-chips
                                        label="Multiple picker in menu"
                                        prepend-icon="mdi-calendar"
                                        readonly
                                        v-bind="attrs"
                                        v-on="on"
                                    ></v-combobox>-->
                </v-card-actions>
                <v-divider/>

                <v-card-subtitle>
                  Key points
                </v-card-subtitle>
                <v-card-actions>
                  <v-row style="width: 100%">
                    <v-col>
                      <v-btn block text elevation="1">
                        Add key points
                      </v-btn>
                    </v-col>
                  </v-row>
                </v-card-actions>
                <v-card-actions>
                  <v-row>
                    <v-col>

                      <v-list
                          subheader
                          two-line
                      >
<!--                        <v-subheader inset>Folders</v-subheader>-->

                        <v-list-item
                            v-for="key_point in key_points"
                            :key="key_point.title"
                        >
                          <v-list-item-avatar>
                            <v-icon
                                class="grey lighten-1"
                                dark
                            >
                              mdi-bullseye
                            </v-icon>
                          </v-list-item-avatar>

                          <v-list-item-content>
                            <v-list-item-title v-text="key_point.title"></v-list-item-title>

                            <v-list-item-subtitle v-text="key_point.subtitle"></v-list-item-subtitle>
                          </v-list-item-content>

                          <v-list-item-action>
                            <v-checkbox
                            ></v-checkbox>
<!--                            <v-btn icon>
                              <v-icon color="grey lighten-1">mdi-information</v-icon>
                            </v-btn>-->
                          </v-list-item-action>
                        </v-list-item>
                      </v-list>
                    </v-col>
                  </v-row>
                </v-card-actions>
                <v-divider/>

                <v-card-subtitle>
                  About it
                </v-card-subtitle>
                <v-card-actions>
                  <v-textarea outlined color="purple">
                  </v-textarea>
                </v-card-actions>

              </v-card>
            </v-col>
          </v-row>

        </v-card-actions>


        <!--ADD -->
        <v-card-actions>


          <v-row>
            <!-- CARD ADD NEW GOAL       -->
            <v-col>
              <v-card outlined min-width="" width="350" min-height="350">

                <v-card-actions>
                  <v-btn color="primary" @click="sendNewGoalToServer()">
                    ADD
                  </v-btn>
                </v-card-actions>

                <!--    GOAL   TEXT    AREA       -->
                <v-card-actions>
                  <v-textarea outlined v-model="new_goal_text"></v-textarea>
                </v-card-actions>

                <!-- select   GOAL   TYPE       -->
                <v-card-actions>
                  <v-row>
                    <v-col
                        cols="12"
                        sm="6"
                    >
                      <v-select
                          v-model="value"
                          :items="items"
                          chips
                          label="Goal Types"
                          multiple
                          outlined
                      ></v-select>
                    </v-col>
                    <v-col
                        cols="12"
                        sm="6"
                    />
                  </v-row>
                </v-card-actions>

                <v-card-actions>
                  <v-menu
                      ref="menu"
                      v-model="menu"
                      :close-on-content-click="false"
                      :return-value.sync="date"
                      transition="scale-transition"
                      offset-y
                      min-width="auto"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                          v-model="date"
                          label="Goal set date"
                          prepend-icon="mdi-calendar"
                          readonly
                          v-bind="attrs"
                          v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker
                        v-model="date"
                        no-title
                        scrollable
                    >
                      <v-spacer></v-spacer>
                      <v-btn
                          text
                          color="primary"
                          @click="menu = false"
                      >
                        Cancel
                      </v-btn>
                      <v-btn
                          text
                          color="primary"
                          @click="$refs.menu.save(date)"
                      >
                        OK
                      </v-btn>
                    </v-date-picker>
                  </v-menu>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
        </v-card-actions>

        <v-card-subtitle>
          all my goals
        </v-card-subtitle>

        <v-card-actions>
          <v-flex d-flex>
            <v-layout wrap>
              <v-flex md4 v-for="goal in this.all_goals" :key="goal.goalID">
                <v-card class="card-container">
                  <v-card-text>
                    {{ goal.goalText }}
                  </v-card-text>
                </v-card>
                <!--                <v-card width="200" v-for="goal in all_goals">
                                  <v-card-text>
                                    {{goal.goalText}}
                                  </v-card-text>
                                </v-card>-->
              </v-flex>
            </v-layout>
          </v-flex>
        </v-card-actions>
      </v-card>

    </v-container>

  </v-content>
</template>

<script>
import axios from "axios";

export default {
  name: "GoalsPage",

  created() {
    this.loadAllUsersGoals();

    this.loadAllGoalTypes();

  },

  data() {
    return {
      key_points: [
        {
          'title': 'kek',
          'subtitle': 'keks',
        },
        {
          'title': 'koko',
          'subtitle': 'keks111',
        },
        {
          'title': 'moooort',
          'subtitle': 'keks222',
        },
        {
          'title': 'mdaaa',
          'subtitle': 'keks333',
        },
      ],

      dates: [],

      new_goal_text: '',
      new_goal_label: '',
      all_goals: [],

      items: [],
      value: [],

      date: new Date().toISOString().substr(0, 10),
      menu: false,
      modal: false,
      menu2: false,

    }
  },

  methods: {

    loadAllGoalTypes() {
      const sendURL = '/api/goals/load/types';

      axios.get(sendURL).then(response => {
        console.log('response', response)
        this.items = response.data
      })

    },

    loadAllUsersGoals() {

      const sendURL = '/api/goals/load/all';

      axios.get(sendURL).then(response => {
        console.log('response', response)
        this.all_goals = response.data;
      })

    },

    sendNewGoalToServer() {

      let newGoalData = {
        goalText: this.new_goal_text,
        new_goal_label: this.new_goal_label


      }

      const sendURL = '/api/goals/add/new'

      axios.post(sendURL, newGoalData).then(response => {
        console.log('response', response);
        this.all_goals.push(response.data)
      })


    }

  },

  computed: {

    distanceToBottom() {

    }

  }
}
</script>

<style scoped>

</style>