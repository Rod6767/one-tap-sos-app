package com.example.data

data class FirstAidStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val tip: String? = null
)

data class FirstAidGuide(
    val id: String,
    val title: String,
    val category: String, // Critical, Medical, Trauma, Environmental
    val priority: String, // IMMEDIATE, URGENT, PREPAREDNESS
    val summary: String,
    val warning: String,
    val steps: List<FirstAidStep>,
    val doList: List<String>,
    val doNotList: List<String>,
    val hasMetronome: Boolean = false // e.g. for CPR at 100-120 BPM
)

object FirstAidData {
    val guides = listOf(
        FirstAidGuide(
            id = "cpr_adult",
            title = "CPR (Cardiopulmonary Resuscitation)",
            category = "Life-Threatening",
            priority = "IMMEDIATE",
            summary = "Hands-only CPR for unresponsive person who is not breathing normally.",
            warning = "Immediately dial Emergency Services (911/112/999) or instruct a bystander to call and find an AED.",
            hasMetronome = true,
            steps = listOf(
                FirstAidStep(
                    stepNumber = 1,
                    title = "Check Responsiveness & Breathing",
                    description = "Tap shoulders firmly and shout 'Are you OK?'. Look at the chest for 5-10 seconds to see if they are breathing normally.",
                    tip = "Agonal gasping is NOT normal breathing. Start CPR immediately."
                ),
                FirstAidStep(
                    stepNumber = 2,
                    title = "Call Emergency & Get AED",
                    description = "Dial local emergency line on speakerphone. Ask someone nearby to fetch an Automated External Defibrillator (AED).",
                    tip = "Put your phone on speaker so dispatchers can coach you."
                ),
                FirstAidStep(
                    stepNumber = 3,
                    title = "Hand Placement & Posture",
                    description = "Place the heel of one hand in the center of the chest (lower half of sternum). Interlock your other hand on top. Lock your elbows straight.",
                    tip = "Position your shoulders directly over your hands."
                ),
                FirstAidStep(
                    stepNumber = 4,
                    title = "Compress Fast & Hard (100-120 BPM)",
                    description = "Push down at least 2 inches (5 cm) deep at a tempo of 100-120 beats per minute (rhythm of 'Stayin Alive'). Allow full chest recoil between pushes.",
                    tip = "Use the built-in metronome tool below to keep the exact 110 BPM pace."
                ),
                FirstAidStep(
                    stepNumber = 5,
                    title = "Continue Until Help Arrives",
                    description = "Do not stop compressions until trained medical personnel take over, an AED is ready to analyze, or the person visibly wakes up.",
                    tip = "If exhausted, swap rescuers every 2 minutes with minimal interruption."
                )
            ),
            doList = listOf(
                "Push hard and push fast in the center of the chest",
                "Allow full chest recoil after every single compression",
                "Use an AED as soon as one becomes available"
            ),
            doNotList = listOf(
                "Do NOT stop compressions for more than 10 seconds",
                "Do NOT lean on the chest between compressions",
                "Do NOT delay starting CPR while checking for a weak pulse"
            )
        ),
        FirstAidGuide(
            id = "choking_heimlich",
            title = "Choking & Heimlich Maneuver",
            category = "Airway Obstruction",
            priority = "IMMEDIATE",
            summary = "Emergency protocol for conscious adults and children unable to breathe, cough, or speak.",
            warning = "If victim loses consciousness, lower them gently to the floor and immediately begin CPR.",
            steps = listOf(
                FirstAidStep(
                    stepNumber = 1,
                    title = "Confirm Choking & Ask for Consent",
                    description = "Ask 'Are you choking? Can you speak?'. If they nod and cannot make sound or clutch their throat (universal sign), act fast.",
                    tip = "If they can cough forcefully, encourage them to keep coughing."
                ),
                FirstAidStep(
                    stepNumber = 2,
                    title = "5 Firm Back Blows",
                    description = "Stand behind and slightly to the side. Support their chest with one hand, lean them forward, and deliver 5 sharp blows between shoulder blades with the heel of your hand.",
                    tip = "Gravity assists in dislodging the object when leaning forward."
                ),
                FirstAidStep(
                    stepNumber = 3,
                    title = "5 Abdominal Thrusts (Heimlich)",
                    description = "Stand behind them, wrap your arms around their waist. Make a fist with one hand and place the thumb side just above the navel (below ribs). Grasp fist with other hand.",
                    tip = "Thrust inward and upward with quick, distinct motions."
                ),
                FirstAidStep(
                    stepNumber = 4,
                    title = "Alternate Back Blows & Thrusts",
                    description = "Repeat the cycle of 5 back blows and 5 abdominal thrusts until the airway clears or the person becomes unresponsive.",
                    tip = "For pregnant or obese individuals, place hands in center of chest instead of abdomen."
                )
            ),
            doList = listOf(
                "Encourage coughing if they can still make sound",
                "Deliver inward and upward abdominal thrusts",
                "Call emergency services immediately if object doesn't clear quickly"
            ),
            doNotList = listOf(
                "Do NOT perform blind finger sweeps in the mouth (it may push object deeper)",
                "Do NOT hit their back while they are standing upright without leaning forward",
                "Do NOT give water to someone who is choking"
            )
        ),
        FirstAidGuide(
            id = "severe_bleeding",
            title = "Severe Bleeding & Tourniquet",
            category = "Trauma",
            priority = "IMMEDIATE",
            summary = "Controlling life-threatening arterial or severe venous hemorrhage.",
            warning = "Arterial bleeding (spurting bright red blood) can be fatal within 3 minutes. Apply continuous intense pressure.",
            steps = listOf(
                FirstAidStep(
                    stepNumber = 1,
                    title = "Direct Firm Pressure",
                    description = "Place a clean cloth, sterile gauze, or gloved hand directly over the wound. Press down with maximum force using your full body weight.",
                    tip = "Maintain uninterrupted pressure for at least 5-10 minutes."
                ),
                FirstAidStep(
                    stepNumber = 2,
                    title = "Pack Deep Wounds",
                    description = "For deep penetrating wounds in junctional areas (groin, shoulder, neck), tightly pack gauze or cloth directly into the wound cavity, then apply pressure.",
                    tip = "Keep pushing gauze in until the cavity is packed tight."
                ),
                FirstAidStep(
                    stepNumber = 3,
                    title = "Apply Tourniquet (Limbs Only)",
                    description = "If bleeding on an arm or leg does not stop with direct pressure, place a commercial tourniquet 2-3 inches above the wound (never over a joint). Tighten until bleeding stops completely.",
                    tip = "Note the exact time of application on the tourniquet or patient's forehead."
                ),
                FirstAidStep(
                    stepNumber = 4,
                    title = "Treat for Shock",
                    description = "Keep the injured person lying down, cover them with a blanket to keep warm, and elevate legs 12 inches if no spinal or fracture injury.",
                    tip = "Do not give them food or water."
                )
            ),
            doList = listOf(
                "Press as hard as you can directly onto the source of bleeding",
                "Add more cloth/gauze on top if blood soaks through — never remove the base layer",
                "Tighten tourniquet until the pulse beyond the wound disappears and bleeding stops"
            ),
            doNotList = listOf(
                "Do NOT remove the initial blood-soaked dressing (it ruins clotting)",
                "Do NOT loosen or remove a tourniquet once applied",
                "Do NOT apply a tourniquet directly over a joint (elbow or knee)"
            )
        ),
        FirstAidGuide(
            id = "stroke_fast",
            title = "Stroke Recognition (FAST Protocol)",
            category = "Neurological",
            priority = "IMMEDIATE",
            summary = "Recognize acute stroke symptoms instantly and trigger emergency dispatch.",
            warning = "Every minute counts. Clot-busting medication must be administered within a strict 3 to 4.5 hour window.",
            steps = listOf(
                FirstAidStep(
                    stepNumber = 1,
                    title = "F - Face Drooping",
                    description = "Ask the person to smile. Does one side of the face droop or look uneven/numb?",
                    tip = "Check for asymmetry in corner of mouth or eyelids."
                ),
                FirstAidStep(
                    stepNumber = 2,
                    title = "A - Arm Weakness",
                    description = "Ask them to raise both arms in front of them with palms up. Does one arm drift downward or feel numb?",
                    tip = "They may describe heaviness or inability to grasp objects."
                ),
                FirstAidStep(
                    stepNumber = 3,
                    title = "S - Speech Difficulty",
                    description = "Ask them to repeat a simple sentence like 'The sky is blue'. Is speech slurred, strange, or are they unable to speak?",
                    tip = "They may also have difficulty understanding spoken language."
                ),
                FirstAidStep(
                    stepNumber = 4,
                    title = "T - Time to Call Emergency",
                    description = "If you observe ANY of these signs, call Emergency Services immediately. Note the exact time symptoms first began.",
                    tip = "Tell the dispatcher: 'I suspect a stroke, symptom onset was at [Time]'."
                )
            ),
            doList = listOf(
                "Note exact time when patient was last seen normal",
                "Keep them in a comfortable resting position with head slightly elevated",
                "Stay calm and monitor breathing until paramedics arrive"
            ),
            doNotList = listOf(
                "Do NOT give aspirin, food, water, or medication (could cause choking or worsen hemorrhagic stroke)",
                "Do NOT let the person sleep or drive themselves to the hospital",
                "Do NOT wait to see if symptoms improve on their own"
            )
        ),
        FirstAidGuide(
            id = "burns_care",
            title = "Burns & Scalds Care",
            category = "Trauma",
            priority = "URGENT",
            summary = "Proper cooling and sterile protection for thermal, chemical, and electrical burns.",
            warning = "Never apply ice, butter, grease, or toothpaste to burns — these trap heat and cause tissue necrosis.",
            steps = listOf(
                FirstAidStep(
                    stepNumber = 1,
                    title = "Cool Immediately with Running Water",
                    description = "Cool the burn under cool or lukewarm running water for 20 continuous minutes. This halts the burning process in skin layers.",
                    tip = "Do this as soon as possible after the burn occurs."
                ),
                FirstAidStep(
                    stepNumber = 2,
                    title = "Remove Constrictive Items",
                    description = "Gently remove rings, watches, tight clothing, or belts near the burned area before swelling starts.",
                    tip = "Leave clothing that is stuck directly to the melted burn intact."
                ),
                FirstAidStep(
                    stepNumber = 3,
                    title = "Cover with Clean, Non-Stick Material",
                    description = "Loosely cover the burned area with clean plastic cling wrap or a sterile non-adherent dressing to protect nerve endings.",
                    tip = "Plastic wrap minimizes pain by preventing air contact with open nerves."
                ),
                FirstAidStep(
                    stepNumber = 4,
                    title = "Seek Immediate Emergency Care If...",
                    description = "Seek urgent care if burn is larger than victim's palm, on face/hands/joints/groin, causes white or charred skin, or from chemical/electrical source.",
                    tip = "Chemical burns should be flushed with copious water for 30+ minutes."
                )
            ),
            doList = listOf(
                "Flush with clean cool running water for 20 minutes",
                "Cover loosely with sterile dressing or clean plastic wrap",
                "Keep the patient warm to prevent hypothermia during large flushes"
            ),
            doNotList = listOf(
                "Do NOT pop blisters (blisters protect against infection)",
                "Do NOT use ice, ice water, or freezing compresses",
                "Do NOT apply oils, butter, ointments, or adhesive bandages directly on raw burns"
            )
        ),
        FirstAidGuide(
            id = "seizure_safety",
            title = "Seizure First Aid",
            category = "Neurological",
            priority = "URGENT",
            summary = "Keeping someone safe during and after an epileptic or febrile convulsion.",
            warning = "Never hold someone down or place any object in their mouth during a seizure.",
            steps = listOf(
                FirstAidStep(
                    stepNumber = 1,
                    title = "Clear the Surrounding Area",
                    description = "Gently guide them to the floor if standing. Move hard, sharp, or hot objects away to prevent injury during convulsions.",
                    tip = "Place a soft folded jacket or pillow under their head."
                ),
                FirstAidStep(
                    stepNumber = 2,
                    title = "Time the Seizure",
                    description = "Check your watch or phone to track duration. If the active seizure lasts longer than 5 minutes, dial Emergency Services immediately.",
                    tip = "Also call if it is their first seizure or if they have repeated seizures."
                ),
                FirstAidStep(
                    stepNumber = 3,
                    title = "Turn onto Side (Recovery Position)",
                    description = "Once jerking stops (or if vomiting/fluid occurs), turn them onto their side to keep their airway open and prevent choking.",
                    tip = "Loosen tight neckwear or ties."
                ),
                FirstAidStep(
                    stepNumber = 4,
                    title = "Stay and Reassure",
                    description = "Stay with them until they are fully awake, alert, and able to answer basic orientation questions.",
                    tip = "Speak in a calm, reassuring tone as they may be confused or disoriented."
                )
            ),
            doList = listOf(
                "Cushion their head with something soft",
                "Turn them on their side once shaking stops",
                "Time the exact length of the seizure"
            ),
            doNotList = listOf(
                "Do NOT put fingers, spoons, or anything into their mouth (they will NOT swallow their tongue)",
                "Do NOT restrain their movements or hold them down",
                "Do NOT offer food, drink, or medication until they are completely awake and responsive"
            )
        ),
        FirstAidGuide(
            id = "earthquake_disaster",
            title = "Earthquake & Disaster Action",
            category = "Disaster Preparedness",
            priority = "PREPAREDNESS",
            summary = "Life-saving actions during strong seismic activity and severe structural collapse risks.",
            warning = "Do NOT run outside while shaking is occurring. Falling glass, facade bricks, and power lines cause most injuries.",
            steps = listOf(
                FirstAidStep(
                    stepNumber = 1,
                    title = "DROP down onto Hands and Knees",
                    description = "Drop where you are. This position protects you from being knocked down and allows you to stay low and crawl to shelter.",
                    tip = "Act immediately at the first sign of rumbling or shaking."
                ),
                FirstAidStep(
                    stepNumber = 2,
                    title = "COVER Head and Neck",
                    description = "Take cover under a sturdy desk or table. If no shelter nearby, cover your head and neck with both arms and lean against an interior wall.",
                    tip = "Stay away from glass windows, mirrors, and tall unsecured furniture."
                ),
                FirstAidStep(
                    stepNumber = 3,
                    title = "HOLD ON Until Shaking Stops",
                    description = "Hold onto your shelter with one hand and move with it if it shifts. Protect your head and neck with your other arm.",
                    tip = "Expect aftershocks and remain alert."
                ),
                FirstAidStep(
                    stepNumber = 4,
                    title = "Evacuate Safely & Check Hazards",
                    description = "Once shaking stops, check for gas leaks (smell of sulfur), electrical sparks, or structural cracks. Use stairs, NEVER elevators.",
                    tip = "Grab your emergency kit and move to an open area away from buildings and utility poles."
                )
            ),
            doList = listOf(
                "Drop, Cover, and Hold On under sturdy furniture",
                "If in bed, stay there and cover head with pillows",
                "If outdoors, move to an open area away from power lines, trees, and buildings"
            ),
            doNotList = listOf(
                "Do NOT stand under doorways (modern doorways are not stronger than rest of house)",
                "Do NOT run outside during active shaking",
                "Do NOT use matches, lighters, or open flames after an earthquake due to potential gas leaks"
            )
        )
    )
}
