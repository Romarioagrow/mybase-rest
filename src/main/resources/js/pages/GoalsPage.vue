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

              <!--goal-add-new-card              -->
              <v-card max-width="500">
                <v-card-title>
                  <v-icon>
                    mdi-target
                  </v-icon>
                  <span>&nbsp;</span>

                  <v-text-field
                      height="50"
                      placeholder="Your new goal"
                      color="purple"

                  >
                  </v-text-field>
                  <!--                  <span>&nbsp;My Goal objective is gain $1000</span>-->
                </v-card-title>
                <v-divider/>

                <v-card-subtitle>
                  Set Types of The Goal
                </v-card-subtitle>
                <v-card-actions>
                  <v-expansion-panels tile>
                    <v-expansion-panel

                    >
                      <v-expansion-panel-header>
                        Select Goal Types
                      </v-expansion-panel-header>
                      <v-expansion-panel-content>
                        <v-item-group multiple>
                          <v-subheader>Types</v-subheader>
                          <v-item
                              v-for="n in goal_types"
                              :key="n"
                              v-slot="{ active, toggle }"
                          >
                            <v-chip
                                active-class="purple--text"
                                :input-value="active"
                                @click="toggle, goal_type_toggle_action(n)"
                            >
                              {{ n }}
                            </v-chip>
                          </v-item>
                        </v-item-group>
                      </v-expansion-panel-content>
                    </v-expansion-panel>
                  </v-expansion-panels>
                </v-card-actions>

<!--                -->
                <v-card-actions>
                  <v-item-group multiple>
                    <v-subheader>Selected Types</v-subheader>
                    <v-item
                        v-for="n in selected_goal_types"
                        :key="n"
                        v-slot="{ active, toggle }"
                    >
                      <v-chip
                          close
                          active-class="purple--text"
                          :input-value="active"
                          @click="toggle"
                          @click:close="delete_selected_goal_type(n)"
                      >
                        {{ n }}
                      </v-chip>
                    </v-item>
                  </v-item-group>
                </v-card-actions>
                <v-divider/>

                <v-card-subtitle>
                  Timing
                </v-card-subtitle>
                <v-card-actions>
                  <v-row>
                    <v-col>
                      <span>Set start date</span><br>
                      <v-date-picker
                          width="220"
                          v-model="set_start_date"
                          color="green lighten-1"
                          header-color="primary"
                      ></v-date-picker>
                    </v-col>
                    <v-col>
                      <span>Plan finished date</span><br>
                      <v-date-picker
                          v-model="set_finish_date"
                          width="220"
                          color="green lighten-1"
                      ></v-date-picker>
                    </v-col>
                  </v-row>
                </v-card-actions>
                <v-divider/>

                <v-card-subtitle>
                  Key points
                </v-card-subtitle>
                <v-card-actions>
                  <v-row style="width: 100%">
                    <v-col>
                      <v-expansion-panels popout flat>
                        <v-expansion-panel
                        >
                          <v-expansion-panel-header>Add key point</v-expansion-panel-header>
                          <v-expansion-panel-content>
                            <v-row>
                              <v-col cols="9">
                                <v-text-field v-model="add_keypoint_name " label="Point name"></v-text-field>
                                <v-text-field v-model="add_keypoint_description" label="Point description"></v-text-field>
                              </v-col>
                              <v-col>
                                <v-btn outlined style="margin-top: 50%" @click="add_key_point_to_goal()">
<!--                                  <v-icon>
                                    mdi-plus
                                  </v-icon>-->
                                  <span>ADD</span>
                                </v-btn>
                              </v-col>
                            </v-row>
                          </v-expansion-panel-content>
                        </v-expansion-panel>
                      </v-expansion-panels>

                    </v-col>
                  </v-row>
                </v-card-actions>

<!--                -->
                <v-card-actions>
                  <v-row>
                    <v-col>
                      <v-list subheader two-line>
                        <!--                        <v-subheader inset>Folders</v-subheader>-->
                        <v-list-item
                            v-for="key_point in key_points"
                            :key="key_point.keypoint_name"
                        >
                          <v-list-item-avatar>
                            <v-icon class="grey lighten-1" dark
                            >
                              mdi-bullseye
                            </v-icon>
                          </v-list-item-avatar>
                          <v-list-item-content>
                            <v-list-item-title v-text="key_point.keypoint_name"></v-list-item-title>
                            <v-list-item-subtitle v-text="key_point.keypoint_description"></v-list-item-subtitle>
                          </v-list-item-content>
                          <v-list-item-action>
                            <v-checkbox/>
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

            <v-col>
              <!--goal-new-card              -->
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
                            v-for="key_point in key_pointsHARD"
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
                          :items="goal_types"
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
      add_keypoint_name: '',
      add_keypoint_description: '',

      set_start_date: '',
      set_finish_date: '',

      key_points: [

      ],

      key_pointsHARD: [
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

      goal_types: [],
      selected_goal_types: [],
      value: [],

      date: new Date().toISOString().substr(0, 10),
      menu: false,
      modal: false,
      menu2: false,

    }
  },

  methods: {
    add_key_point_to_goal() {

      const newKeyPoint = {
        'keypoint_name': this.add_keypoint_name,
        'keypoint_description': this.add_keypoint_description,
      }

      this.key_points.push(newKeyPoint)

      this.add_keypoint_name = ''
      this.add_keypoint_description = ''

      //add_keypoint_name
      //add_keypoint_description
    },

    delete_selected_goal_type(goal_type) {

      const index = this.selected_goal_types.indexOf(goal_type)
      this.selected_goal_types.splice(index, 1)

    },

    goal_type_toggle_action(goal_type) {
      console.log('goal_type', goal_type)
      console.log('selected_goal_types', this.selected_goal_types)

      if (!this.selected_goal_types.includes(goal_type)) {

        this.selected_goal_types.push(goal_type)
      } else {
        const index = this.selected_goal_types.indexOf(goal_type)
        this.selected_goal_types.splice(index, 1)
      }
    },

    loadAllGoalTypes() {
      const sendURL = '/api/goals/load/types';

      axios.get(sendURL).then(response => {
        console.log('response', response)
        this.goal_types = response.data
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