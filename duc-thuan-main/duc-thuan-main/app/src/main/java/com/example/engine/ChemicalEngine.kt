package com.example.engine

import com.example.model.*

object ChemicalEngine {

    val CURRICULUM_EXPERIMENTS: List<ExperimentTemplate> = listOf(
        ExperimentTemplate(
            id = "exp_zn_hcl",
            titleVi = "Kẽm tác dụng dung dịch Axit clohiđric",
            titleEn = "Zinc reaction with Hydrochloric acid",
            reactantAId = "zn",
            reactantBId = "hcl",
            category = ReactionType.METAL_DISPLACEMENT,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = false,
            summaryVi = "Kim loại đứng trước Hydro giải phóng khí H2 sủi bọt, tỏa nhiệt (ΔrH° < 0).",
            summaryEn = "Active metal displaces hydrogen forming gas bubbles, exothermic.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.METAL_DISPLACEMENT,
                balancedEquation = "Zn(s) + 2HCl(aq) → ZnCl₂(aq) + H₂(g)↑",
                ionicEquation = "Zn + 2H⁺ + 2Cl⁻ → Zn²⁺ + 2Cl⁻ + H₂↑",
                netIonicEquation = "Zn(s) + 2H⁺(aq) → Zn²⁺(aq) + H₂(g)↑",
                deltaH = -153.9f,
                tempDelta = 12.5f,
                solutionFinalColor = 0x15E0F7FA,
                gasFormula = "H2",
                gasRate = 0.85f,
                gasColor = 0x88FFFFFF,
                phenomenaVi = "Mẩu kẽm tan dần, bọt khí không màu (H2) thoát ra nhanh quanh bề mặt kim loại, thành ống nghiệm nóng lên rõ rệt.",
                phenomenaEn = "Zinc dissolving, rapid bubbles of colorless H2 gas, test tube warms up noticeably.",
                microExplanationVi = "Tại bề mặt kim loại Zn, mỗi nguyên tử kẽm nhường 2 electron: Zn → Zn²⁺ + 2e⁻. Các ion H⁺ trong dung dịch nhận electron tạo nguyên tử H, rồi ghép đôi thành phân tử H₂ dạng khí bay lên.",
                microExplanationEn = "At Zn crystal surface, zinc atoms lose 2 electrons to form Zn²⁺ cations. Protons (H⁺) gain electrons to form diatomic H2 gas.",
                competencyId = "metal_series"
            )
        ),
        ExperimentTemplate(
            id = "exp_cu_hno3",
            titleVi = "Đồng tác dụng Axit nitric đặc (Thí nghiệm độc)",
            titleEn = "Copper reaction with concentrated Nitric acid",
            reactantAId = "cu",
            reactantBId = "hno3_conc",
            category = ReactionType.REDOX,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = true,
            summaryVi = "Thí nghiệm nguy hiểm cấm làm tự do ở trường do sinh khí NO2 nâu đỏ cực độc.",
            summaryEn = "Hazardous experiment generating toxic nitrogen dioxide (NO2) brown gas.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.REDOX,
                balancedEquation = "Cu(s) + 4HNO₃(đặc) → Cu(NO₃)₂(aq) + 2NO₂(g)↑ + 2H₂O(l)",
                ionicEquation = "Cu + 4H⁺ + 4NO₃⁻ → Cu²⁺ + 2NO₃⁻ + 2NO₂↑ + 2H₂O",
                netIonicEquation = "Cu(s) + 4H⁺(aq) + 2NO₃⁻(aq) → Cu²⁺(aq) + 2NO₂(g)↑ + 2H₂O(l)",
                deltaH = -135.0f,
                tempDelta = 19.0f,
                solutionFinalColor = 0xCC0052CC, // Deep blue Cu2+
                gasFormula = "NO2",
                gasRate = 0.95f,
                gasColor = 0xAA8D6E63, // Brown gas
                isDangerous = true,
                dangerWarningVi = "CẢNH BÁO KHÍ ĐỘC NO2: Gây phù phổi cấp nếu hít phải. Mô phỏng ảo thay thế thí nghiệm thật!",
                phenomenaVi = "Lá đồng tan nhanh, dung dịch chuyển sang màu xanh lam đậm, sinh nhiều luồng khí màu nâu đỏ bốc lên có mùi hắc đặc trưng.",
                phenomenaEn = "Copper dissolves rapidly, solution turns intense azure blue, dense reddish-brown fumes of NO2 evolve.",
                microExplanationVi = "Cu bị oxi hóa thành Cu²⁺. Ion nitrat (NO₃⁻) trong môi trường axit đặc đóng vai trò chất oxi hóa mạnh, nhận electron tạo ra khí NO₂ (Số oxi hóa của N giảm từ +5 xuống +4).",
                microExplanationEn = "Cu is oxidized to Cu²⁺. Nitrate ion in acidic solution serves as powerful oxidizer, reduced from N(+5) to toxic N(+4) in NO2.",
                competencyId = "redox_thermo"
            )
        ),
        ExperimentTemplate(
            id = "exp_na_h2o",
            titleVi = "Natri tác dụng Nước cất (Cháy nổ mãnh liệt)",
            titleEn = "Sodium reaction with Water (Violent Flame)",
            reactantAId = "na",
            reactantBId = "h2o",
            category = ReactionType.REDOX,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = true,
            summaryVi = "Natri nóng chảy vo tròn lướt trên mặt nước, tỏa nhiệt bốc cháy ngọn lửa vàng rực rỡ.",
            summaryEn = "Sodium melts into sphere, skitters across water surface, igniting yellow flame.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.REDOX,
                balancedEquation = "2Na(s) + 2H₂O(l) → 2NaOH(aq) + H₂(g)↑",
                ionicEquation = "2Na + 2H₂O → 2Na⁺ + 2OH⁻ + H₂↑",
                netIonicEquation = "2Na(s) + 2H₂O(l) → 2Na⁺(aq) + 2OH⁻(aq) + H₂(g)↑",
                deltaH = -368.4f,
                tempDelta = 26.0f,
                solutionFinalColor = 0x2280DEEA,
                gasFormula = "H2",
                gasRate = 1.0f,
                gasColor = 0xAAFFFFFF,
                hasFlame = true,
                isDangerous = true,
                dangerWarningVi = "CẢNH BÁO NỔ/CHÁY: Trong thực tế chỉ được lấy mẩu Na bằng hạt vừng/đậu xanh, cấm để rơi vào mắt hoặc da!",
                phenomenaVi = "Mẩu Natri nổi trên mặt nước, nóng chảy thành viên cầu sáng loáng chạy lướt tròn, bốc cháy ngọn lửa màu vàng chói kèm tiếng nổ lách tách.",
                phenomenaEn = "Sodium melts into a sphere skittering on water surface, igniting with intense yellow flame and sizzling sound.",
                microExplanationVi = "Natri có năng lượng ion hóa rất thấp, ngay lập tức nhường electron cho phân tử nước: Na → Na⁺ + e⁻; 2H₂O + 2e⁻ → H₂ + 2OH⁻. Lượng nhiệt tỏa ra cực lớn làm nóng chảy Na (nhiệt độ nóng chảy 98°C) và đốt cháy khí H₂ trong không khí.",
                microExplanationEn = "Sodium loses its valence electron rapidly to water: Na → Na⁺ + e⁻; 2H2O + 2e⁻ → H2 + 2OH⁻. Massive exothermic energy melts sodium and ignites the evolved H2 gas.",
                competencyId = "redox_thermo"
            )
        ),
        ExperimentTemplate(
            id = "exp_fe_cuso4",
            titleVi = "Đinh sắt tác dụng dung dịch Đồng(II) sunfat",
            titleEn = "Iron nail reaction with Copper(II) sulfate",
            reactantAId = "fe",
            reactantBId = "cuso4",
            category = ReactionType.METAL_DISPLACEMENT,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = false,
            summaryVi = "Sắt khử ion Cu²⁺ tạo kim loại đồng đỏ bám ngoài, màu xanh lam nhạt dần.",
            summaryEn = "Iron reduces Cu²⁺ forming reddish copper coat on nail, blue fades.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.METAL_DISPLACEMENT,
                balancedEquation = "Fe(s) + CuSO₄(aq) → FeSO₄(aq) + Cu(s)↓",
                ionicEquation = "Fe + Cu²⁺ + SO₄²⁻ → Fe²⁺ + SO₄²⁻ + Cu↓",
                netIonicEquation = "Fe(s) + Cu²⁺(aq) → Fe²⁺(aq) + Cu(s)↓",
                deltaH = -152.4f,
                tempDelta = 4.5f,
                solutionFinalColor = 0x5581C784, // Pale green Fe2+
                precipitateFormula = "Cu",
                precipitateColor = 0xFFB87333,
                phenomenaVi = "Màu xanh lam của dung dịch CuSO4 nhạt dần rồi chuyển sang xanh lục nhạt (Fe2+). Trên bề mặt đinh sắt xuất hiện lớp đồng màu đỏ cam bám chặt.",
                phenomenaEn = "Blue solution fades to light green. Reddish copper metal deposits on the submerged iron surface.",
                microExplanationVi = "Thế điện cực chuẩn E°(Fe²⁺/Fe) = -0.44V nhỏ hơn E°(Cu²⁺/Cu) = +0.34V. Do đó kim loại Fe nhường electron cho cation Cu²⁺ trong dung dịch, đẩy Cu tự do ra bám vào mạng kim loại.",
                microExplanationEn = "Because standard reduction potential of Fe is lower than Cu, metallic Fe transfers electrons to aqueous Cu²⁺ ions, displacing pure Cu metal.",
                competencyId = "metal_series"
            )
        ),
        ExperimentTemplate(
            id = "exp_bacl2_h2so4",
            titleVi = "Bari clorua tác dụng Axit sunfuric",
            titleEn = "Barium chloride reaction with Sulfuric acid",
            reactantAId = "bacl2",
            reactantBId = "h2so4",
            category = ReactionType.ION_EXCHANGE,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = false,
            summaryVi = "Phản ứng trao đổi ion tạo kết tủa trắng BaSO4 đặc trưng không tan trong axit.",
            summaryEn = "Ion exchange producing characteristic dense white precipitate of BaSO4.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.ION_EXCHANGE,
                balancedEquation = "BaCl₂(aq) + H₂SO₄(aq) → BaSO₄(s)↓ + 2HCl(aq)",
                ionicEquation = "Ba²⁺ + 2Cl⁻ + 2H⁺ + SO₄²⁻ → BaSO₄↓ + 2H⁺ + 2Cl⁻",
                netIonicEquation = "Ba²⁺(aq) + SO₄²⁻(aq) → BaSO₄(s)↓",
                deltaH = -26.0f,
                tempDelta = 2.0f,
                solutionFinalColor = 0x15E0F7FA,
                precipitateFormula = "BaSO4",
                precipitateColor = 0xFFFFFFFF,
                phenomenaVi = "Ngay lập tức xuất hiện kết tủa trắng đục như sữa, lắng dần xuống đáy ống nghiệm. Kết tủa này không tan kể cả khi thêm axit HCl dư.",
                phenomenaEn = "Immediate milk-white precipitate of BaSO4 appears, settling to the bottom. Insoluble even in excess acid.",
                microExplanationVi = "Lực hút tĩnh điện giữa ion Ba²⁺ và SO₄²⁻ cực kỳ mạnh, vượt qua năng lượng solvat hóa của nước, khiến chúng liên kết thành mạng tinh thể ion BaSO4 không tan.",
                microExplanationEn = "Strong electrostatic attraction between Ba²⁺ and SO₄²⁻ exceeds water hydration energy, forming insoluble ionic BaSO4 lattice.",
                competencyId = "ion_exchange"
            )
        ),
        ExperimentTemplate(
            id = "exp_agno3_nacl",
            titleVi = "Bạc nitrat tác dụng Natri clorua",
            titleEn = "Silver nitrate reaction with Sodium chloride",
            reactantAId = "agno3",
            reactantBId = "nacl",
            category = ReactionType.ION_EXCHANGE,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = false,
            summaryVi = "Thuốc thử nhận biết ion halogenua (Cl-) tạo kết tủa vón trắng AgCl.",
            summaryEn = "Analytical precipitation test identifying chloride ions with AgCl.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.ION_EXCHANGE,
                balancedEquation = "AgNO₃(aq) + NaCl(aq) → AgCl(s)↓ + NaNO₃(aq)",
                ionicEquation = "Ag⁺ + NO₃⁻ + Na⁺ + Cl⁻ → AgCl↓ + Na⁺ + NO₃⁻",
                netIonicEquation = "Ag⁺(aq) + Cl⁻(aq) → AgCl(s)↓",
                deltaH = -65.5f,
                tempDelta = 3.0f,
                solutionFinalColor = 0x15EDE7F6,
                precipitateFormula = "AgCl",
                precipitateColor = 0xFFF5F5F5,
                phenomenaVi = "Xuất hiện kết tủa trắng vón của bạc clorua (AgCl), để ngoài ánh sáng kết tủa dần chuyển sang màu tím xám rồi đen do Ag bị khử.",
                phenomenaEn = "Curdy white precipitate of AgCl forms, turns greyish-purple in sunlight due to photodecomposition to Ag.",
                microExplanationVi = "Ion Ag⁺ tương tác nhanh với ion Cl⁻ tạo kết tủa AgCl với tích số tan T = 1.8 × 10⁻¹⁰ rất bé, là phản ứng đặc trưng để định tính ion Cl⁻.",
                microExplanationEn = "Ag⁺ and Cl⁻ ions combine to form AgCl with very low solubility product (Ksp = 1.8e-10), ideal for qualitative halide test.",
                competencyId = "ion_exchange"
            )
        ),
        ExperimentTemplate(
            id = "exp_kmno4_hcl",
            titleVi = "Kali pemanganat tác dụng Axit clohiđric đặc (Điều chế Cl2)",
            titleEn = "KMnO4 reaction with conc. HCl (Chlorine preparation)",
            reactantAId = "kmno4",
            reactantBId = "hcl",
            category = ReactionType.REDOX,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = true,
            summaryVi = "Điều chế khí Clo màu vàng lục độc hại, thể hiện tính oxi hóa mạnh của MnO4-.",
            summaryEn = "Preparation of toxic yellow-green Chlorine gas via strong oxidation.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.REDOX,
                balancedEquation = "2KMnO₄(s) + 16HCl(aq) → 2KCl(aq) + 2MnCl₂(aq) + 5Cl₂(g)↑ + 8H₂O(l)",
                ionicEquation = "2MnO₄⁻ + 10Cl⁻ + 16H⁺ → 2Mn²⁺ + 5Cl₂↑ + 8H₂O",
                netIonicEquation = "2MnO₄⁻(aq) + 10Cl⁻(aq) + 16H⁺(aq) → 2Mn²⁺(aq) + 5Cl₂(g)↑ + 8H₂O(l)",
                deltaH = -215.0f,
                tempDelta = 15.0f,
                solutionFinalColor = 0x22F8BBD0, // Pale pink / colorless Mn2+
                gasFormula = "Cl2",
                gasRate = 0.90f,
                gasColor = 0xAAFFEB3B, // Yellow-green
                isDangerous = true,
                dangerWarningVi = "CẢNH BÁO KHÍ CLO ĐỘC: Khí Cl2 phá hủy niêm mạc đường hô hấp. Tuyệt đối không tự ý làm ngoài tủ hút!",
                phenomenaVi = "Tinh thể thuốc tím tan, dung dịch tím đậm mất màu dần chuyển thành hồng nhạt/không màu (Mn2+), sủi luồng khí màu vàng lục có mùi xốc nồng (Cl2).",
                phenomenaEn = "Deep purple disappears to pale pink (Mn2+), copious pungent yellow-green chlorine gas evolves.",
                microExplanationVi = "Mn(+7) trong ion pemanganat bị khử xuống Mn(+2). Ion clorua Cl⁻ bị oxi hóa từ -1 lên số oxi hóa 0 trong đơn chất khí Cl₂.",
                microExplanationEn = "Mn(+7) in permanganate is reduced to Mn(+2). Chloride ions are oxidized to elemental Cl2 gas.",
                competencyId = "redox_thermo"
            )
        ),
        ExperimentTemplate(
            id = "exp_caco3_hcl",
            titleVi = "Đá vôi (Canxi cacbonat) tác dụng Axit clohiđric",
            titleEn = "Calcium carbonate reaction with Hydrochloric acid",
            reactantAId = "caco3",
            reactantBId = "hcl",
            category = ReactionType.ION_EXCHANGE,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = false,
            summaryVi = "Axit mạnh đẩy axit yếu carbonic ra tạo khí CO2 sủi bọt mạnh.",
            summaryEn = "Strong acid decomposes carbonate to evolve effervescent CO2 gas.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.ION_EXCHANGE,
                balancedEquation = "CaCO₃(s) + 2HCl(aq) → CaCl₂(aq) + CO₂(g)↑ + H₂O(l)",
                ionicEquation = "CaCO₃ + 2H⁺ + 2Cl⁻ → Ca²⁺ + 2Cl⁻ + CO₂↑ + H₂O",
                netIonicEquation = "CaCO₃(s) + 2H⁺(aq) → Ca²⁺(aq) + CO₂(g)↑ + H₂O(l)",
                deltaH = -15.2f,
                tempDelta = 3.5f,
                solutionFinalColor = 0x15E0F7FA,
                gasFormula = "CO2",
                gasRate = 0.80f,
                gasColor = 0x88FFFFFF,
                phenomenaVi = "Mẩu đá vôi tan dần, nhiều bọt khí không màu (CO2) thoát ra làm sủi tăm liên tục. Khí dẫn vào nước vôi trong sẽ làm vẩn đục.",
                phenomenaEn = "Limestone chips dissolve with lively effervescence of colorless CO2 bubbles, turning limewater cloudy.",
                microExplanationVi = "Các ion H⁺ phá vỡ mạng tinh thể cacbonat, tạo axit cacbonic H₂CO₃ không bền, lập tức phân hủy thành khí CO₂ và nước.",
                microExplanationEn = "Protons protonate carbonate ions yielding unstable carbonic acid H2CO3, decomposing into CO2 and H2O.",
                competencyId = "ion_exchange"
            )
        ),
        ExperimentTemplate(
            id = "exp_naoh_hcl",
            titleVi = "Trung hòa Axit clohiđric bằng Natri hiđroxit",
            titleEn = "Neutralization of HCl with Sodium hydroxide",
            reactantAId = "naoh",
            reactantBId = "hcl",
            category = ReactionType.ACID_BASE,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = false,
            summaryVi = "Phản ứng trung hòa kinh điển giữa axit mạnh và bazơ mạnh, tỏa nhiệt.",
            summaryEn = "Classical acid-base neutralization with exothermic enthalpy change.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.ACID_BASE,
                balancedEquation = "NaOH(aq) + HCl(aq) → NaCl(aq) + H₂O(l)",
                ionicEquation = "Na⁺ + OH⁻ + H⁺ + Cl⁻ → Na⁺ + Cl⁻ + H₂O",
                netIonicEquation = "H⁺(aq) + OH⁻(aq) → H₂O(l)",
                deltaH = -57.3f,
                tempDelta = 6.8f,
                solutionFinalColor = 0x15E0F7FA,
                phenomenaVi = "Dung dịch vẫn trong suốt không màu, nhưng nhiệt kế tăng lên do nhiệt trung hòa (ΔrH° = -57.3 kJ/mol). Chỉ thị phenolphthalein từ hồng chuyển sang không màu khi vừa đủ axit.",
                phenomenaEn = "Solution stays clear, digital thermometer registers rise due to enthalpy of neutralization (-57.3 kJ/mol).",
                microExplanationVi = "Bản chất của phản ứng trung hòa axit - bazơ mạnh trong dung dịch nước là cation H⁺ kết hợp với anion OH⁻ tạo phân tử H₂O liên kết cộng hóa trị bền vững.",
                microExplanationEn = "Fundamental neutralization mechanism is the rapid combination of H⁺ and OH⁻ to form covalent water molecules.",
                competencyId = "ion_exchange"
            )
        ),
        ExperimentTemplate(
            id = "exp_cu_hcl",
            titleVi = "Đồng tác dụng Axit clohiđric (Thí nghiệm đối chứng)",
            titleEn = "Copper in Hydrochloric acid (Control test)",
            reactantAId = "cu",
            reactantBId = "hcl",
            category = ReactionType.NO_REACTION,
            isCurriculum2018Highlight = true,
            isDangerousOrExpensive = false,
            summaryVi = "Kim loại đứng sau Hydro trong dãy điện hóa không phản ứng với axit HCl.",
            summaryEn = "Copper is less reactive than hydrogen, no reaction occurs with non-oxidizing acid.",
            expectedOutcome = ReactionOutcome(
                reactionType = ReactionType.NO_REACTION,
                balancedEquation = "Cu(s) + HCl(aq) → Không xảy ra phản ứng",
                ionicEquation = "Cu + H⁺ + Cl⁻ → Không phản ứng",
                netIonicEquation = "Cu(s) + H⁺(aq) → No reaction",
                deltaH = 0f,
                tempDelta = 0f,
                solutionFinalColor = 0x15E0F7FA,
                phenomenaVi = "Lá đồng giữ nguyên màu đỏ cam, không có bọt khí thoát ra, dung dịch trong suốt không đổi màu, nhiệt độ không thay đổi.",
                phenomenaEn = "Copper foil remains unchanged red-orange, no gas bubbles, no temperature change.",
                microExplanationVi = "Trong dãy thế điện cực chuẩn, cặp oxi hóa khử Cu²⁺/Cu có E° = +0.34V > E°(2H⁺/H₂) = 0.00V. Do đó ion H⁺ không đủ thế oxi hóa để lấy electron của kim loại Cu.",
                microExplanationEn = "Standard potential of Cu²⁺/Cu (+0.34V) is higher than 2H⁺/H2 (0.00V), hence H⁺ cannot oxidize metallic Cu under normal conditions.",
                competencyId = "metal_series"
            )
        )
    )

    /**
     * Dynamic Chemical Engine Matrix Evaluator:
     * Evaluates reaction outcome given any pair of reactants A and B.
     */
    fun evaluateReaction(substanceA: Substance, substanceB: Substance): ReactionOutcome {
        val idA = substanceA.id
        val idB = substanceB.id

        // Match against catalog templates first
        for (template in CURRICULUM_EXPERIMENTS) {
            if ((template.reactantAId == idA && template.reactantBId == idB) ||
                (template.reactantAId == idB && template.reactantBId == idA)) {
                return template.expectedOutcome
            }
        }

        // Dynamic rule deduction for sandbox combinations
        // Rule 1: Metal + Acid
        val metal = if (substanceA.state == PhysicalState.SOLID && substanceA.reactivityRank < 90) substanceA
                    else if (substanceB.state == PhysicalState.SOLID && substanceB.reactivityRank < 90) substanceB
                    else null

        val acid = if (substanceA.ph < 4f) substanceA else if (substanceB.ph < 4f) substanceB else null

        if (metal != null && acid != null) {
            if (acid.id == "hno3_conc") {
                return ReactionOutcome(
                    reactionType = ReactionType.REDOX,
                    balancedEquation = "${metal.formula} + HNO₃(đặc) → Muối nitrat + NO₂↑ + H₂O",
                    ionicEquation = "${metal.formula} + H⁺ + NO₃⁻ → Cation kim loại + NO₂↑ + H₂O",
                    netIonicEquation = "${metal.formula} + H⁺ + NO₃⁻ → Phản ứng Oxi hóa - Khử",
                    deltaH = -120f,
                    tempDelta = 14f,
                    solutionFinalColor = 0xAA0288D1,
                    gasFormula = "NO2",
                    gasRate = 0.9f,
                    gasColor = 0xAA8D6E63,
                    isDangerous = true,
                    dangerWarningVi = "Khí NO2 độc hại! Yêu cầu bảo hộ.",
                    phenomenaVi = "Kim loại ${metal.nameVi} tan mạnh trong axit nitric đặc, sủi khí màu nâu đỏ NO2 độc hại và tỏa nhiệt.",
                    phenomenaEn = "${metal.nameIupac} reacts with conc. HNO3 yielding brown NO2 gas and high heat.",
                    microExplanationVi = "Axit nitric đặc đóng vai trò chất oxi hóa mạnh, lấy electron của kim loại và sinh ra khí NO2.",
                    microExplanationEn = "Conc. HNO3 acts as strong oxidizing agent extracting valence electrons.",
                    competencyId = "redox_thermo"
                )
            }

            if (metal.reactivityRank < 11) { // More active than H
                return ReactionOutcome(
                    reactionType = ReactionType.METAL_DISPLACEMENT,
                    balancedEquation = "${metal.formula} + Axit → Muối + H₂↑",
                    ionicEquation = "${metal.formula} + 2H⁺ → ${metal.formula}²⁺ + H₂↑",
                    netIonicEquation = "${metal.formula} + 2H⁺ → ${metal.formula}²⁺ + H₂↑",
                    deltaH = -110f,
                    tempDelta = 8.5f,
                    solutionFinalColor = 0x15E0F7FA,
                    gasFormula = "H2",
                    gasRate = 0.75f,
                    phenomenaVi = "Kim loại ${metal.nameVi} tan dần, sủi bọt khí H2 không màu và tỏa nhiệt nhẹ.",
                    phenomenaEn = "${metal.nameIupac} dissolves, bubbling colorless H2 gas.",
                    microExplanationVi = "Do ${metal.nameVi} đứng trước H trong dãy hoạt động, thế điện cực âm hơn nên khử được ion H+ thành H2.",
                    microExplanationEn = "Metal lies before H in reactivity series, reducing H+ to H2 gas.",
                    competencyId = "metal_series"
                )
            } else {
                return ReactionOutcome(
                    reactionType = ReactionType.NO_REACTION,
                    balancedEquation = "${metal.formula} + ${acid.formula} → Không phản ứng",
                    ionicEquation = "Không xảy ra phản ứng trao đổi hay oxi hóa khử",
                    netIonicEquation = "No reaction",
                    deltaH = 0f,
                    tempDelta = 0f,
                    solutionFinalColor = 0x15E0F7FA,
                    phenomenaVi = "Không có hiện tượng gì xảy ra. Kim loại ${metal.nameVi} đứng sau Hydro trong dãy hoạt động hóa học.",
                    phenomenaEn = "No reaction. Metal is less active than hydrogen.",
                    microExplanationVi = "Ion H+ không đủ thế oxi hóa để oxy hóa kim loại kém hoạt động như ${metal.nameIupac}.",
                    microExplanationEn = "Protons lack sufficient oxidizing potential for inactive metals.",
                    competencyId = "metal_series"
                )
            }
        }

        // Rule 2: Insoluble precipitate rule
        if ((idA == "bacl2" && idB == "cuso4") || (idA == "cuso4" && idB == "bacl2")) {
            return ReactionOutcome(
                reactionType = ReactionType.ION_EXCHANGE,
                balancedEquation = "BaCl₂(aq) + CuSO₄(aq) → BaSO₄(s)↓ + CuCl₂(aq)",
                ionicEquation = "Ba²⁺ + 2Cl⁻ + Cu²⁺ + SO₄²⁻ → BaSO₄↓ + Cu²⁺ + 2Cl⁻",
                netIonicEquation = "Ba²⁺(aq) + SO₄²⁻(aq) → BaSO₄(s)↓",
                deltaH = -24.5f,
                tempDelta = 1.5f,
                solutionFinalColor = 0x880288D1,
                precipitateFormula = "BaSO4",
                precipitateColor = 0xFFFFFFFF,
                phenomenaVi = "Xuất hiện kết tủa trắng đục BaSO4 lắng xuống trong dung dịch màu xanh lam của muối đồng.",
                phenomenaEn = "White precipitate of BaSO4 forms in blue copper solution.",
                microExplanationVi = "Ion Ba2+ và SO4 2- tạo thành hợp chất rất khó tan có Tích số tan Ksp rất nhỏ.",
                microExplanationEn = "Ba2+ and SO4(2-) precipitate out due to extremely low solubility product.",
                competencyId = "ion_exchange"
            )
        }

        // Rule 3: Acid + Base
        val base = if (substanceA.ph > 10f) substanceA else if (substanceB.ph > 10f) substanceB else null
        if (acid != null && base != null) {
            return ReactionOutcome(
                reactionType = ReactionType.ACID_BASE,
                balancedEquation = "${base.formula} + ${acid.formula} → Muối + H₂O",
                ionicEquation = "H⁺ + OH⁻ → H₂O",
                netIonicEquation = "H⁺(aq) + OH⁻(aq) → H₂O(l)",
                deltaH = -57.3f,
                tempDelta = 5.5f,
                solutionFinalColor = 0x15E0F7FA,
                phenomenaVi = "Phản ứng trung hòa tỏa nhiệt, dung dịch trong suốt.",
                phenomenaEn = "Neutralization reaction, solution remains clear and releases heat.",
                microExplanationVi = "Ion H+ kết hợp với OH- tạo thành nước.",
                microExplanationEn = "H+ combines with OH- to form water.",
                competencyId = "ion_exchange"
            )
        }

        // Default: No observable reaction
        return ReactionOutcome(
            reactionType = ReactionType.NO_REACTION,
            balancedEquation = "${substanceA.formula} + ${substanceB.formula} → Không xảy ra phản ứng",
            ionicEquation = "Các ion cùng tồn tại trong dung dịch không tạo kết tủa, khí hay chất điện li yếu",
            netIonicEquation = "No reaction occurred",
            deltaH = 0f,
            tempDelta = 0f,
            solutionFinalColor = substanceB.colorArgb,
            phenomenaVi = "Không có hiện tượng gì xảy ra, hai chất trộn lẫn không có biến đổi hóa học.",
            phenomenaEn = "No chemical change observed, substances simply mix without reacting.",
            microExplanationVi = "Không thỏa mãn điều kiện xảy ra phản ứng trao đổi ion (tạo kết tủa/khí/chất điện li yếu) hoặc điều kiện phản ứng oxi hóa khử.",
            microExplanationEn = "Conditions for chemical reaction (precipitation, gas release, redox driving force) are not met.",
            competencyId = "ion_exchange"
        )
    }
}
