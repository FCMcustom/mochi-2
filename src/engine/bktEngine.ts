import { BktSkillState, AdaptiveQuizQuestion } from '../types';

export const INITIAL_BKT_SKILLS: BktSkillState[] = [
  {
    competencyId: 'metal_series',
    nameVi: 'Dãy hoạt động hóa học & Thế điện cực E°',
    nameEn: 'Metal Reactivity & Standard Electrode Potential',
    descriptionVi: 'Khả năng so sánh tính khử kim loại, dự đoán kim loại đẩy H+ từ axit hoặc đẩy kim loại khác khỏi dung dịch muối theo chuẩn GDPT 2018.',
    priorL0: 0.35,
    probLearnT: 0.22,
    probGuessG: 0.15,
    probSlipS: 0.10,
    currentProb: 0.42,
    totalAttempts: 3,
    correctCount: 2
  },
  {
    competencyId: 'redox_micro',
    nameVi: 'Bản chất vi mô & Chuyển dịch Electron',
    nameEn: 'Microscopic Redox & Electron Transfer',
    descriptionVi: 'Nhận diện quá trình nhường/nhận electron giữa các tiểu phân nguyên tử, ion và sự biến đổi số oxi hóa ở cấp độ nguyên tử.',
    priorL0: 0.25,
    probLearnT: 0.20,
    probGuessG: 0.12,
    probSlipS: 0.08,
    currentProb: 0.38,
    totalAttempts: 2,
    correctCount: 1
  },
  {
    competencyId: 'ion_exchange',
    nameVi: 'Điều kiện phản ứng trao đổi ion trong dung dịch',
    nameEn: 'Ion Exchange Conditions in Solution',
    descriptionVi: 'Vận dụng bản chất dung dịch chất điện li và điều kiện tạo chất kết tủa, chất bay hơi hoặc chất điện li yếu theo phương trình ion thu gọn.',
    priorL0: 0.40,
    probLearnT: 0.25,
    probGuessG: 0.18,
    probSlipS: 0.10,
    currentProb: 0.65,
    totalAttempts: 4,
    correctCount: 3
  },
  {
    competencyId: 'thermo_enthalpy',
    nameVi: 'Năng lượng phản ứng & Biến thiên Enthalpy ΔrH°',
    nameEn: 'Reaction Thermochemistry & Enthalpy Change',
    descriptionVi: 'Hiểu bản chất tỏa nhiệt (ΔrH° < 0), thu nhiệt (ΔrH° > 0), liên hệ giữa năng lượng liên kết bị phá vỡ và tạo thành.',
    priorL0: 0.30,
    probLearnT: 0.18,
    probGuessG: 0.15,
    probSlipS: 0.12,
    currentProb: 0.48,
    totalAttempts: 3,
    correctCount: 2
  }
];

export function updateBktProbability(
  currentProb: number,
  isCorrect: boolean,
  pLearn = 0.20,
  pGuess = 0.15,
  pSlip = 0.10
): number {
  let posterior: number;

  if (isCorrect) {
    const numerator = currentProb * (1 - pSlip);
    const denominator = numerator + (1 - currentProb) * pGuess;
    posterior = denominator > 0 ? numerator / denominator : currentProb;
  } else {
    const numerator = currentProb * pSlip;
    const denominator = numerator + (1 - currentProb) * (1 - pGuess);
    posterior = denominator > 0 ? numerator / denominator : currentProb;
  }

  // Transition update (learning probability)
  const nextProb = posterior + (1 - posterior) * pLearn;
  return Math.min(Math.max(nextProb, 0.01), 0.99);
}

export const ADAPTIVE_QUIZ_BANK: AdaptiveQuizQuestion[] = [
  {
    id: 'q1',
    competencyId: 'metal_series',
    questionVi: 'Vì sao lá đồng (Cu) không có hiện tượng phản ứng khi ngâm trong dung dịch HCl loãng?',
    optionsVi: [
      'A. Đồng là kim loại quý có lớp màng oxit bền ngăn cách.',
      'B. Cặp Cu²⁺/Cu có thế điện cực chuẩn E° = +0.34V lớn hơn thế E° của 2H⁺/H2 (0.00V).',
      'C. Dung dịch axit clohidric không có tính axit đối với kim loại màu đỏ.',
      'D. Đồng có nhiệt độ nóng chảy quá cao nên phản ứng không thể kích hoạt.'
    ],
    correctIndex: 1,
    explanationVi: 'Theo chuẩn GDPT 2018: Cặp Cu²⁺/Cu có thế điện cực chuẩn E° = +0.34V > E°(2H⁺/H2) = 0.00V, do đó ion H⁺ không thể oxi hóa được Cu kim loại trong điều kiện chuẩn.'
  },
  {
    id: 'q2',
    competencyId: 'redox_micro',
    questionVi: 'Trong thí nghiệm Zn tác dụng với dung dịch HCl, ở cấp độ vi mô, hạt nào trực tiếp nhường electron cho ion H⁺?',
    optionsVi: [
      'A. Anion Cl⁻ trong dung dịch muối.',
      'B. Nguyên tử Zn trên bề mặt mạng tinh thể kim loại.',
      'C. Phân tử nước H2O đóng vai trò dung môi.',
      'D. Các phân tử khí H2 vừa được sinh ra.'
    ],
    correctIndex: 1,
    explanationVi: 'Nguyên tử kẽm Zn nhường 2 electron: Zn → Zn²⁺ + 2e⁻. Các electron này dịch chuyển trên bề mặt kim loại và được các cation H⁺ hút nhận: 2H⁺ + 2e⁻ → H2.'
  },
  {
    id: 'q3',
    competencyId: 'ion_exchange',
    questionVi: 'Phương trình ion rút gọn của phản ứng giữa BaCl2 và H2SO4 phản ánh bản chất gì?',
    optionsVi: [
      'A. Sự trao đổi toàn bộ phân tử BaCl2 và H2SO4.',
      'B. Chỉ có sự kết hợp giữa cation Ba²⁺ và anion SO4²⁻ tạo thành chất kết tủa BaSO4.',
      'C. Sự biến đổi số oxi hóa của nguyên tố Bari từ 0 lên +2.',
      'D. Sự tạo thành khí bay hơi HCl.'
    ],
    correctIndex: 1,
    explanationVi: 'Phương trình ion rút gọn Ba²⁺ + SO4²⁻ → BaSO4↓ chỉ ra rằng các ion H⁺ và Cl⁻ không tham gia phản ứng mà vẫn tự do trong dung dịch.'
  },
  {
    id: 'q4',
    competencyId: 'thermo_enthalpy',
    questionVi: 'Khi thực hiện phản ứng trung hòa NaOH + HCl, nhiệt kế chỉ nhiệt độ tăng lên. Điều này chứng tỏ phản ứng có giá trị biến thiên Enthalpy ΔrH° như thế nào?',
    optionsVi: [
      'A. ΔrH° > 0 (phản ứng thu nhiệt từ môi trường xung quanh).',
      'B. ΔrH° = 0 (phản ứng cân bằng nhiệt động học không đổi).',
      'C. ΔrH° < 0 (phản ứng tỏa nhiệt ra môi trường xung quanh).',
      'D. ΔrH° không xác định được khi chưa đo khối lượng nước.'
    ],
    correctIndex: 2,
    explanationVi: 'Nhiệt độ dung dịch tăng lên nghĩa là hệ phản ứng giải phóng nhiệt năng ra môi trường ngoài, tương ứng với biến thiên Enthalpy tiêu chuẩn ΔrH° < 0 (phản ứng tỏa nhiệt).'
  }
];
