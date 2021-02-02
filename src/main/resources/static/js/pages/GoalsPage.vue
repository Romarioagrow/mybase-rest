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
          all my goals
        </v-card-subtitle>


        <!--BUTTON ADD -->
        <v-card-actions >
          <v-row>
<!--            <v-col>
              <v-btn
                  depressed
                  color="primary"
              >
                ПО НАЖАТИЮ
              </v-btn>
            </v-col>-->


            <!-- CARD ADD NEW GOAL       -->
            <v-col>
              <v-card outlined min-width="" width="350" min-height="350">
                <v-card-actions>
                  <v-btn color="primary">
                    ADD
                  </v-btn>
                </v-card-actions>
                <v-card-actions>
                  <v-textarea outlined></v-textarea>
                </v-card-actions>
                <v-card-actions>
                  <v-text-field outlined label="МЕТКА"></v-text-field>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-container>


              </v-container>
            </v-col>
          </v-row>

        </v-card-actions>
      </v-card>




    </v-container>

  </v-content>
</template>

<script>
export default {
  name: "GoalsPage",
  computed: {

    distanceToBottom() {

    }

  }
}
</script>

<style scoped>

</style>