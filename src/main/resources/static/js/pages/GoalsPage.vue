<template>
  <v-content>
    <v-container fluid >

      <!--v-card-title-->
      <v-card>
        <v-card-title class="justify-center">
          <span>myGOALS</span>
        </v-card-title>
      </v-card>


      <!--      -->
      <v-card height='100%' style="position: relative">
        <v-card-subtitle>
          my goals
        </v-card-subtitle>


        <!--BUTTON ADD -->
        <v-card-actions >
          <v-row>
            <!-- CARD ADD NEW GOAL       -->
            <v-col>
              <v-card outlined min-width="" width="350" min-height="350">
                <v-card-actions>
                  <v-btn color="primary" @click="sendNewGoalToServer()">
                    ADD
                  </v-btn>
                </v-card-actions>
                <v-card-actions>
                  <v-textarea outlined v-model="new_goal_text"></v-textarea>
                </v-card-actions>
                <v-card-actions>
                  <v-text-field outlined label="МЕТКА" v-model="new_goal_label"></v-text-field>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>

        </v-card-actions>


        <v-card-subtitle>
          all my goals
        </v-card-subtitle>

        <v-card-actions>

          <v-row>
            <v-card width="200" v-for="goal in all_goals">
              <v-card-text>
                {{goal.goalText}}
              </v-card-text>
            </v-card>

          </v-row>

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


  },

  data() {
    return {

      new_goal_text: '',
      new_goal_label: '',
      all_goals: [],


    }
  },

  methods: {

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

      const sendHost = ''
      const sendURL = '/api/goals/add/new'

      //let sendURL = ''

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