import { Substance, ReactionOutcome, ExperimentTemplate } from '../types';

export const KNOWN_REACTIONS: ReactionOutcome[] = [
  // 1. Zn + HCl (Metal displacement & Redox)
  {
    reactantAId: 'zn',
    reactantBId: 'hcl',
    balancedEquation: 'Zn(s) + 2HCl(aq) → ZnCl2(aq) + H2(g)↑',
    netIonicEquation: 'Zn(s) + 2H+(aq) → Zn2+(aq) + H2(g)↑',
    phenomenonVi: 'Mẩu kẽm tan dần, xuất hiện rất nhiều bọt khí không màu không mùi (H2) thoát ra nhanh và bám quanh mẩu kẽm. Đáy ống nghiệm ấm lên do phản ứng tỏa nhiệt.',
    phenomenonEn: 'Zinc pellet gradually dissolves with vigorous effervescence of colorless hydrogen gas (H2). Test tube warms up.',
    microExplanationVi: 'Nguyên tử kẽm (Zn) nhường 2e tạo thành cation Zn²⁺ tan vào dung dịch: Zn → Zn²⁺ + 2e⁻. Các ion H⁺ hydrat hóa trong dung dịch nhận 2 electron này để tạo thành nguyên tử H, kết hợp lại thành phân tử khí H2 bay lên: 2H⁺ + 2e⁻ → H2.',
    microExplanationEn: 'Zn atom oxidizes by losing 2e- forming Zn2+ in solution. Two H+ ions gain 2e- at the metal surface and recombine into diatomic H2 gas.',
    deltaH: -152.4,
    tempDelta: 14.5,
    hasGas: true,
    gasFormula: 'H2',
    hasPrecipitate: false,
    isViolent: false,
    requiresHeat: false,
    hazardType: 'NONE',
    competencyTarget: 'redox_micro'
  },
  // 2. Na + H2O (Alkali metal - Violent & Exothermic)
  {
    reactantAId: 'na',
    reactantBId: 'h2o',
    balancedEquation: '2Na(s) + 2H2O(l) → 2NaOH(aq) + H2(g)↑',
    netIonicEquation: '2Na(s) + 2H2O(l) → 2Na+(aq) + 2OH-(aq) + H2(g)↑',
    phenomenonVi: '⚠️ NGUY HIỂM: Mẩu Natri nóng chảy ngay lập tức vo tròn thành viên bi bạc lướt nhanh trên mặt nước, phát ra tiếng rít xèo xèo, bốc cháy ngọn lửa màu vàng chói sáng và bắn tia lửa!',
    phenomenonEn: '⚠️ VIOLENT: Sodium melts into a silvery bead skittering across water surface, igniting with a bright yellow flame and sparks.',
    microExplanationVi: 'Nguyên tử Na có 1 electron lớp ngoài cùng (3s¹) với bán kính lớn, liên kết kim loại yếu. Năng lượng ion hóa thấp khiến Na nhường ngay 1e cho H2O: Na → Na⁺ + e⁻. Phân tử nước bị khử: 2H2O + 2e⁻ → 2OH⁻ + H2. Lượng nhiệt tỏa ra cực lớn làm nóng chảy Na (nhiệt độ nóng chảy chỉ 97.8°C).',
    microExplanationEn: 'Low ionization energy of Na (3s1) causes rapid electron transfer to H2O molecules, producing Na+ and OH- while reducing H to H2 with massive exothermic heat release.',
    deltaH: -368.6,
    tempDelta: 42.0,
    hasGas: true,
    gasFormula: 'H2',
    hasPrecipitate: false,
    flameColor: '#FFD54F',
    isViolent: true,
    requiresHeat: false,
    hazardType: 'EXPLOSION',
    competencyTarget: 'metal_series'
  },
  // 3. Cu + HNO3 conc (Redox with toxic NO2 gas)
  {
    reactantAId: 'cu',
    reactantBId: 'hno3_conc',
    balancedEquation: 'Cu(s) + 4HNO3(conc) → Cu(NO3)2(aq) + 2NO2(g)↑ + 2H2O(l)',
    netIonicEquation: 'Cu(s) + 4H+(aq) + 2NO3-(aq) → Cu2+(aq) + 2NO2(g)↑ + 2H2O(l)',
    phenomenonVi: '⚠️ ĐỘC HẠI: Dung dịch sủi bọt mãnh liệt và chuyển sang màu xanh lam đậm. Khí Nitrogen dioxide (NO2) màu nâu đỏ bốc lên cuồn cuộn có mùi hắc đặc trưng.',
    phenomenonEn: 'Vigorous bubbling, solution turns deep azure blue, dense reddish-brown toxic NO2 gas fumes vigorously from test tube.',
    microExplanationVi: 'HNO3 đặc là chất oxi hóa rất mạnh nhờ ion NO3⁻ trong môi trường axit mạnh. Cu(0) bị oxi hóa lên Cu²⁺: Cu → Cu²⁺ + 2e⁻ (tạo màu xanh lam). N(+5) trong NO3⁻ nhận 1e bị khử xuống N(+4) trong NO2: NO3⁻ + 2H⁺ + 1e⁻ → NO2 + H2O.',
    microExplanationEn: 'Cu is oxidized to Cu2+ (forming blue aquo complexes), while nitrate N(+5) is reduced to nitrogen dioxide N(+4), producing brown NO2 gas.',
    deltaH: -188.0,
    tempDelta: 22.0,
    hasGas: true,
    gasFormula: 'NO2',
    hasPrecipitate: false,
    isViolent: true,
    requiresHeat: false,
    hazardType: 'TOXIC_GAS',
    competencyTarget: 'redox_micro'
  },
  // 4. BaCl2 + H2SO4 (Ion exchange & Precipitation)
  {
    reactantAId: 'bacl2',
    reactantBId: 'h2so4',
    balancedEquation: 'BaCl2(aq) + H2SO4(aq) → BaSO4(s)↓ + 2HCl(aq)',
    netIonicEquation: 'Ba2+(aq) + SO4 2-(aq) → BaSO4(s)↓',
    phenomenonVi: 'Xuất hiện ngay lập tức kết tủa trắng đục như sữa lơ lửng trong dung dịch và lắng dần xuống đáy ống nghiệm. Không có bọt khí, nhiệt độ gần như không đổi.',
    phenomenonEn: 'Immediate milky white precipitate (BaSO4) forms and gradually sediments at the bottom of the tube.',
    microExplanationVi: 'Cation Ba²⁺ tự do va chạm với anion SO4²⁻. Năng lượng mạng tinh thể của BaSO4 rất lớn vượt trội so với năng lượng hydrat hóa, các ion liên kết ion chặt chẽ tạo thành mạng tinh thể muối không tan BaSO4 tách khỏi pha lỏng.',
    microExplanationEn: 'Free Ba2+ and SO4(2-) ions collide and crystallize due to extremely high crystal lattice enthalpy over hydration enthalpy, forming insoluble BaSO4.',
    deltaH: -26.0,
    tempDelta: 1.5,
    hasGas: false,
    hasPrecipitate: true,
    precipitateFormula: 'BaSO4',
    precipitateColor: '#FFFFFF',
    isViolent: false,
    requiresHeat: false,
    hazardType: 'NONE',
    competencyTarget: 'ion_exchange'
  },
  // 5. Cu + HCl (Counter-example: No reaction)
  {
    reactantAId: 'cu',
    reactantBId: 'hcl',
    balancedEquation: 'Cu(s) + HCl(aq) → Không phản ứng (No Reaction)',
    netIonicEquation: 'Cu(s) + H+(aq) → Không xảy ra phản ứng',
    phenomenonVi: 'Không có hiện tượng gì xảy ra: Mẩu đồng vẫn giữ nguyên màu đỏ ánh kim, dung dịch trong suốt không màu, không sủi bọt khí.',
    phenomenonEn: 'No reaction observed. Copper piece remains metallic lustrous, no bubbling, solution stays clear.',
    microExplanationVi: 'Theo chuẩn thế điện cực GDPT 2018: Cặp Cu²⁺/Cu có thế điện cực chuẩn E° = +0.34V, lớn hơn thế điện cực chuẩn của cặp 2H⁺/H2 (E° = 0.00V). Do đó, ion H⁺ không đủ thế oxi hóa để nhận electron từ kim loại Cu.',
    microExplanationEn: 'E°(Cu2+/Cu) = +0.34V is higher than E°(2H+/H2) = 0.00V. Therefore, H+ ions lack sufficient reduction potential to oxidize Cu.',
    deltaH: 0,
    tempDelta: 0,
    hasGas: false,
    hasPrecipitate: false,
    isViolent: false,
    requiresHeat: false,
    hazardType: 'NONE',
    competencyTarget: 'metal_series'
  },
  // 6. NaOH + HCl (Acid-base neutralization)
  {
    reactantAId: 'naoh',
    reactantBId: 'hcl',
    balancedEquation: 'NaOH(aq) + HCl(aq) → NaCl(aq) + H2O(l)',
    netIonicEquation: 'H+(aq) + OH-(aq) → H2O(l)',
    phenomenonVi: 'Dung dịch vẫn trong suốt không màu, không có bọt khí hay kết tủa, nhưng nhiệt kế đo được nhiệt độ tăng vọt lên rõ rệt (phản ứng trung hòa tỏa nhiệt).',
    phenomenonEn: 'Clear colorless solution without bubbles or precipitates, but thermometer shows rapid temperature spike (exothermic neutralization).',
    microExplanationVi: 'Ion H⁺ từ axit và ion OH⁻ từ bazơ kết hợp với nhau tạo thành phân tử nước H2O bền vững: H⁺ + OH⁻ → H2O. Năng lượng tạo liên kết O-H giải phóng Enthalpy trung hòa tiêu chuẩn ΔrH° ≈ -57.3 kJ/mol.',
    microExplanationEn: 'H+ and OH- ions combine to form stable covalent H2O molecules, releasing the standard molar enthalpy of neutralization (-57.3 kJ/mol).',
    deltaH: -57.3,
    tempDelta: 9.8,
    hasGas: false,
    hasPrecipitate: false,
    isViolent: false,
    requiresHeat: false,
    hazardType: 'NONE',
    competencyTarget: 'thermo_enthalpy'
  },
  // 7. Fe + CuSO4 (Displacement)
  {
    reactantAId: 'fe',
    reactantBId: 'cuso4',
    balancedEquation: 'Fe(s) + CuSO4(aq) → FeSO4(aq) + Cu(s)↓',
    netIonicEquation: 'Fe(s) + Cu2+(aq) → Fe2+(aq) + Cu(s)↓',
    phenomenonVi: 'Màu xanh lam của dung dịch nhạt dần chuyển sang màu xanh lục nhạt. Bề mặt đinh sắt xuất hiện lớp kim loại màu đỏ gạch bám chắc vào (kim loại Đồng giải phóng).',
    phenomenonEn: 'Blue color of solution fades to pale green. Reddish-brown metallic copper deposits firmly onto the iron surface.',
    microExplanationVi: 'Fe đứng trước Cu trong dãy điện hóa (E° Fe²⁺/Fe = -0.44V < E° Cu²⁺/Cu = +0.34V). Nguyên tử Fe nhường 2e cho ion Cu²⁺: Fe → Fe²⁺ + 2e⁻, sau đó Cu²⁺ + 2e⁻ → Cu bám lên bề mặt sắt.',
    microExplanationEn: 'Fe has lower standard reduction potential than Cu. Fe atoms transfer 2 electrons to Cu2+ cations, reducing them to metallic Cu layer on the iron nail.',
    deltaH: -152.0,
    tempDelta: 6.2,
    hasGas: false,
    hasPrecipitate: true,
    precipitateFormula: 'Cu',
    precipitateColor: '#B87333',
    isViolent: false,
    requiresHeat: false,
    hazardType: 'NONE',
    competencyTarget: 'metal_series'
  },
  // 8. H2O + H2SO4 conc (Edge-case Safety Hazard: Water into Concentrated Sulfuric Acid -> Exothermic Boil)
  {
    reactantAId: 'h2o',
    reactantBId: 'h2so4_conc',
    balancedEquation: 'H2O(l) + H2SO4(conc 98%) → H3O+(aq) + HSO4-(aq) (Sôi bùng nguy hiểm!)',
    netIonicEquation: 'H2O + H2SO4 → H3O+ + HSO4- (Hydrat hóa cực mạnh, ΔH tỏa nhiệt bùng nổ)',
    phenomenonVi: '⚠️ VI PHẠM QUY TẮC AN TOÀN (EXOTHERMIC BOIL): Rót nước vào axit sunfuric đặc làm tỏa nhiệt khổng lồ cục bộ ở lớp mặt. Nước sôi bùng tức thì, làm bắn tung tóe những giọt axit đặc nóng 100°C ra xung quanh kèm theo khói sương axit trắng mù mịt!',
    phenomenonEn: '⚠️ CRITICAL SAFETY HAZARD (EXOTHERMIC BOIL): Pouring water into concentrated sulfuric acid causes instantaneous flash boiling at the surface, violently splattering scalding acid droplets and dense white vapor!',
    microExplanationVi: 'Khối lượng riêng của H2SO4 đặc (d = 1.84 g/mL) lớn hơn nhiều so với nước (d = 1.00 g/mL). Khi đổ nước vào axit, nước nhẹ hơn nổi lên trên. Phản ứng hydrat hóa H2SO4 tỏa nhiệt quá lớn tập trung tại bề mặt tiếp xúc mỏng, ngay lập tức đẩy nhiệt độ nước lên quá 100°C làm nước sôi mãnh liệt và bắn tung tóe các giọt axit đặc.',
    microExplanationEn: 'Dense H2SO4 (1.84 g/mL) stays below lighter water (1.00 g/mL). The immense exothermic heat of hydration concentrated in the thin boundary layer flash-boils water into steam, splashing acid violently.',
    deltaH: -95.3,
    tempDelta: 78.0,
    hasGas: true,
    gasFormula: 'Khói hơi axit (Acid mist)',
    hasPrecipitate: false,
    isViolent: true,
    requiresHeat: false,
    hazardType: 'EXOTHERMIC_BOIL',
    competencyTarget: 'thermo_enthalpy'
  }
];

export const CURRICULUM_EXPERIMENTS: ExperimentTemplate[] = [
  {
    id: 'exp_zn_hcl',
    titleVi: 'Thí nghiệm Kẽm tác dụng với Axit Clohidric (Dãy hoạt động & Oxi hóa khử)',
    titleEn: 'Zinc with Hydrochloric Acid (Activity Series & Redox)',
    reactantAId: 'zn',
    reactantBId: 'hcl',
    summaryVi: 'Nghiên cứu quá trình kim loại đứng trước Hydro giải phóng khí H2 và sự chuyển dịch electron ở cấp độ vi mô.',
    summaryEn: 'Investigate displacement of hydrogen by reactive metal and electron transfer at microscopic scale.',
    category: 'METAL_DISPLACEMENT',
    categoryLabelVi: 'Thế kim loại & H2',
    isDangerousOrExpensive: false,
    expectedOutcome: KNOWN_REACTIONS[0]
  },
  {
    id: 'exp_na_h2o',
    titleVi: 'Thí nghiệm Natri tác dụng với Nước (Kim loại kiềm - Nguy cơ nổ)',
    titleEn: 'Sodium with Water (Alkali Metal - Explosion Hazard)',
    reactantAId: 'na',
    reactantBId: 'h2o',
    summaryVi: '⚠️ Thí nghiệm nguy hiểm cao: Khảo sát tính khử cực mạnh của kim loại kiềm nhóm IA, hiện tượng nóng chảy và phát tia lửa.',
    summaryEn: 'High hazard experiment: Study strong reducing power of group IA alkali metal, melting and flame phenomena.',
    category: 'REDOX',
    categoryLabelVi: 'Kim loại kiềm & Nước',
    isDangerousOrExpensive: true,
    expectedOutcome: KNOWN_REACTIONS[1]
  },
  {
    id: 'exp_cu_hno3',
    titleVi: 'Thí nghiệm Đồng tác dụng với Axit Nitric đặc (Sinh khí độc NO2)',
    titleEn: 'Copper with Concentrated Nitric Acid (Toxic NO2 Gas)',
    reactantAId: 'cu',
    reactantBId: 'hno3_conc',
    summaryVi: '⚠️ Độc hại: Khảo sát tính oxi hóa mạnh của gốc nitrat trong môi trường axit mạnh, sinh khí NO2 màu nâu đỏ.',
    summaryEn: 'Toxic experiment: Investigate oxidative power of nitrate in strong acid, producing brown NO2 fumes.',
    category: 'REDOX',
    categoryLabelVi: 'Oxi hóa - Khử',
    isDangerousOrExpensive: true,
    expectedOutcome: KNOWN_REACTIONS[2]
  },
  {
    id: 'exp_bacl2_h2so4',
    titleVi: 'Thí nghiệm Phản ứng trao đổi ion tạo kết tủa BaSO4',
    titleEn: 'Ion Exchange Reaction Forming BaSO4 Precipitate',
    reactantAId: 'bacl2',
    reactantBId: 'h2so4',
    summaryVi: 'Khảo sát điều kiện xảy ra phản ứng trao đổi ion trong dung dịch chất điện li (tạo chất kết tủa không tan trong axit).',
    summaryEn: 'Examine conditions for ion exchange in electrolyte solutions (formation of acid-insoluble precipitate).',
    category: 'ION_EXCHANGE',
    categoryLabelVi: 'Trao đổi ion',
    isDangerousOrExpensive: false,
    expectedOutcome: KNOWN_REACTIONS[3]
  },
  {
    id: 'exp_cu_hcl_counter',
    titleVi: 'Thí nghiệm Đối chứng: Đồng với Axit Clohidric (Không phản ứng)',
    titleEn: 'Control Experiment: Copper with Hydrochloric Acid (No Reaction)',
    reactantAId: 'cu',
    reactantBId: 'hcl',
    summaryVi: 'Thí nghiệm phản chứng giúp học sinh khắc sâu quy luật dãy điện hóa: Kim loại đứng sau H không đẩy được H ra khỏi dung dịch axit loãng.',
    summaryEn: 'Counter-example experiment proving electrochemical series: Metals after hydrogen cannot displace H+ from dilute acids.',
    category: 'NO_REACTION',
    categoryLabelVi: 'Thí nghiệm đối chứng',
    isDangerousOrExpensive: false,
    expectedOutcome: KNOWN_REACTIONS[4]
  },
  {
    id: 'exp_naoh_hcl',
    titleVi: 'Thí nghiệm Phản ứng trung hòa Axit - Bazơ & Enthalpy tạo thành',
    titleEn: 'Acid-Base Neutralization & Enthalpy of Neutralization',
    reactantAId: 'naoh',
    reactantBId: 'hcl',
    summaryVi: 'Đo lường biến thiên Enthalpy phản ứng tỏa nhiệt khi cation H+ kết hợp với anion OH- tạo thành nước.',
    summaryEn: 'Measure exothermic enthalpy change as H+ and OH- ions combine to form water molecules.',
    category: 'NEUTRALIZATION',
    categoryLabelVi: 'Nhiệt trung hòa',
    isDangerousOrExpensive: false,
    expectedOutcome: KNOWN_REACTIONS[5]
  },
  {
    id: 'exp_fe_cuso4',
    titleVi: 'Thí nghiệm Sắt đẩy Đồng ra khỏi dung dịch CuSO4',
    titleEn: 'Iron Displacing Copper from CuSO4 Solution',
    reactantAId: 'fe',
    reactantBId: 'cuso4',
    summaryVi: 'Quan sát hiện tượng phủ kim loại đồng đỏ lên đinh sắt và sự đổi màu dung dịch từ xanh lam sang xanh lục nhạt.',
    summaryEn: 'Observe deposition of reddish copper onto iron and color shift of solution from blue to light green.',
    category: 'METAL_DISPLACEMENT',
    categoryLabelVi: 'Kim loại đẩy kim loại',
    isDangerousOrExpensive: false,
    expectedOutcome: KNOWN_REACTIONS[6]
  },
  {
    id: 'exp_h2o_h2so4_hazard',
    titleVi: 'Thí nghiệm Vi phạm An toàn: Rót nước vào H2SO4 đặc (Sôi bùng Exothermic Boil)',
    titleEn: 'Hazard Protocol Simulation: Water into Conc H2SO4 (Flash Exothermic Boil)',
    reactantAId: 'h2o',
    reactantBId: 'h2so4_conc',
    summaryVi: '⚠️ Mô phỏng tai nạn vi phạm an toàn tiêu chuẩn GDPT 2018: Rót nước vào axit đặc gây sôi bùng bắn tung tóe axit và tỏa nhiệt khổng lồ.',
    summaryEn: 'Simulation of critical safety protocol violation: Pouring water into concentrated sulfuric acid causing flash boiling.',
    category: 'NEUTRALIZATION',
    categoryLabelVi: 'Quy tắc An toàn & Sôi bùng',
    isDangerousOrExpensive: true,
    expectedOutcome: KNOWN_REACTIONS[7]
  }
];

export function findReactionOutcome(subA?: Substance | null, subB?: Substance | null): ReactionOutcome | null {
  if (!subA || !subB) return null;
  const idA = subA.id;
  const idB = subB.id;

  const found = KNOWN_REACTIONS.find(
    r => (r.reactantAId === idA && r.reactantBId === idB) ||
         (r.reactantAId === idB && r.reactantBId === idA)
  );

  if (found) return found;

  // Generic fallback for combinations without designated chemical reaction
  return {
    reactantAId: idA,
    reactantBId: idB,
    balancedEquation: `${subA.formula} + ${subB.formula} → Hỗn hợp hòa trộn cơ học (Không có phản ứng hóa học)`,
    netIonicEquation: 'Không có sự biến đổi liên kết hóa học',
    phenomenonVi: 'Hai chất chỉ hòa trộn hoặc khuấy đều vật lý, không xuất hiện hiện tượng hóa học (không có bọt khí, không kết tủa, nhiệt độ không đổi).',
    phenomenonEn: 'Physical mixing only, no chemical reaction observed.',
    microExplanationVi: 'Các phân tử hoặc ion chỉ phân tán xen kẽ giữa các phân tử dung môi mà không xảy ra quá trình đứt gãy hoặc hình thành liên kết hóa học mới.',
    microExplanationEn: 'No chemical bond breakage or formation occurs between the mixed particles.',
    deltaH: 0,
    tempDelta: 0,
    hasGas: false,
    hasPrecipitate: false,
    isViolent: false,
    requiresHeat: false,
    hazardType: 'NONE',
    competencyTarget: 'metal_series'
  };
}
