import { Substance, ReactionOutcome } from '../types';

export interface PhAnalysisResult {
  phValue: number;
  pOhValue: number;
  hPlusMolarity: number;
  ohMinusMolarity: number;
  classification: 'STRONG_ACID' | 'WEAK_ACID' | 'NEUTRAL' | 'WEAK_BASE' | 'STRONG_BASE';
  classificationVi: string;
  litmusColorHex: string;
  litmusDescriptionVi: string;
  phenolphthaleinColorHex: string;
  phenolphthaleinDescriptionVi: string;
  universalColorHex: string;
  explanationVi: string;
}

/**
 * Calculates theoretical pH and indicator responses based on reactants and reaction status
 * in accordance with Vietnam GDPT 2018 Chemistry curriculum (Grade 11 - Topic 1: Ionic Equilibrium).
 */
export function calculateSolutionPh(
  reactantA: Substance | null,
  reactantB: Substance | null,
  outcome: ReactionOutcome | null,
  isReactionActive: boolean
): PhAnalysisResult {
  let ph = 7.0;
  let explanation = 'Dung dịch trung tính (nước cất), [H+] = [OH-] = 1.0 × 10⁻⁷ M.';

  const aId = reactantA?.id;
  const bId = reactantB?.id;

  // Case 1: Active reaction or completed reaction
  if (isReactionActive && outcome) {
    // 1. Zn + HCl -> ZnCl2 + H2
    if ((aId === 'zn' && bId === 'hcl') || (aId === 'hcl' && bId === 'zn')) {
      ph = 4.8;
      explanation = 'Axit HCl bị tiêu hao giải phóng H2↑. Dung dịch chứa muối ZnCl2 bị thủy phân một phần tạo môi trường axit yếu (pH ≈ 4.8).';
    }
    // 2. Na + H2O -> NaOH + H2
    else if ((aId === 'na' && bId === 'h2o') || (aId === 'h2o' && bId === 'na')) {
      ph = 13.8;
      explanation = 'Natri tác dụng mãnh liệt với nước tạo dung dịch kiềm mạnh NaOH (nồng độ OH⁻ cao), pH vọt lên 13.8.';
    }
    // 3. Cu + HNO3 conc -> Cu(NO3)2 + 2NO2 + 2H2O
    else if ((aId === 'cu' && bId === 'hno3_conc') || (aId === 'hno3_conc' && bId === 'cu')) {
      ph = 0.3;
      explanation = 'Dung dịch chứa axit nitric HNO3 đặc còn dư và ion Cu²⁺, môi trường axit rất mạnh (pH ≈ 0.3).';
    }
    // 4. BaCl2 + H2SO4 -> BaSO4 + 2HCl
    else if ((aId === 'bacl2' && bId === 'h2so4') || (aId === 'h2so4' && bId === 'bacl2')) {
      ph = 0.5;
      explanation = 'Tạo kết tủa trắng BaSO4 và giải phóng axit clohidric HCl tự do trong dung dịch, môi trường axit mạnh (pH ≈ 0.5).';
    }
    // 5. NaOH + HCl -> NaCl + H2O (Neutralization)
    else if ((aId === 'naoh' && bId === 'hcl') || (aId === 'hcl' && bId === 'naoh')) {
      ph = 7.0;
      explanation = 'Phản ứng trung hòa hoàn toàn H⁺ + OH⁻ → H2O. Dung dịch muối NaCl không bị thủy phân, môi trường trung tính chuẩn (pH = 7.0).';
    }
    // 6. Fe + CuSO4 -> FeSO4 + Cu
    else if ((aId === 'fe' && bId === 'cuso4') || (aId === 'cuso4' && bId === 'fe')) {
      ph = 5.2;
      explanation = 'Tạo muối FeSO4. Cation Fe²⁺ thủy phân một phần cho môi trường axit yếu (pH ≈ 5.2).';
    }
    // 7. H2O + H2SO4 conc
    else if ((aId === 'h2o' && bId === 'h2so4_conc') || (aId === 'h2so4_conc' && bId === 'h2o')) {
      ph = 0.1;
      explanation = 'Dung dịch axit sunfuric đặc hydrat hóa tỏa nhiệt dữ dội, môi trường siêu axit (pH ≈ 0.1).';
    }
  } else {
    // Unreacted / individual reactants present
    const aqueousOrAcid = [reactantA, reactantB].find(
      (s) => s && (s.physicalState === 'AQUEOUS' || s.physicalState === 'LIQUID')
    );

    if (aId === 'hcl' || bId === 'hcl') {
      ph = 0.0;
      explanation = 'Dung dịch axit clohidric HCl 1.0 M phân li hoàn toàn: [H+] = 1.0 M ⇒ pH = -log(1.0) = 0.0.';
    } else if (aId === 'h2so4' || bId === 'h2so4') {
      ph = 0.3;
      explanation = 'Dung dịch axit sunfuric H2SO4 0.5 M phân li nấc 1 hoàn toàn, [H+] ≈ 0.5 - 1.0 M ⇒ pH ≈ 0.3.';
    } else if (aId === 'h2so4_conc' || bId === 'h2so4_conc' || aId === 'hno3_conc' || bId === 'hno3_conc') {
      ph = 0.0;
      explanation = 'Axit vô cơ đặc nồng độ cao, lực axit cực mạnh ([H+] > 1 M) ⇒ pH ≈ 0.0.';
    } else if (aId === 'naoh' || bId === 'naoh') {
      ph = 14.0;
      explanation = 'Dung dịch kiềm mạnh NaOH 1.0 M phân li hoàn toàn: [OH-] = 1.0 M ⇒ pOH = 0 ⇒ pH = 14.0.';
    } else if (aId === 'cuso4' || bId === 'cuso4') {
      ph = 4.2;
      explanation = 'Dung dịch muối CuSO4: Cation Cu²⁺ thủy phân thuận nghịch tạo H⁺: Cu²⁺ + H2O ⇌ Cu(OH)⁺ + H⁺ (pH ≈ 4.2).';
    } else if (aId === 'bacl2' || bId === 'bacl2') {
      ph = 7.0;
      explanation = 'Dung dịch muối BaCl2 tạo từ bazơ mạnh Ba(OH)2 và axit mạnh HCl nên không bị thủy phân, pH = 7.0.';
    } else if (aqueousOrAcid?.id === 'h2o') {
      ph = 7.0;
      explanation = 'Nước cất tinh khiết, tích số ion của nước Kw = [H+][OH-] = 10⁻¹⁴ ở 25°C ⇒ pH = 7.0.';
    } else {
      ph = 7.0;
      explanation = 'Chưa chọn dung dịch chất điện li hoặc dung môi thí nghiệm.';
    }
  }

  // Derived properties
  const pOh = Math.max(0, Math.min(14, +(14 - ph).toFixed(1)));
  const hPlus = Math.pow(10, -ph);
  const ohMinus = Math.pow(10, -pOh);

  let classification: PhAnalysisResult['classification'] = 'NEUTRAL';
  let classificationVi = 'Môi trường Trung tính';
  let litmusColorHex = '#a855f7'; // Purple
  let litmusDescriptionVi = 'Giấy quỳ tím giữ nguyên màu tím chuẩn';
  let phenolphthaleinColorHex = '#f8fafc'; // Colorless
  let phenolphthaleinDescriptionVi = 'Không màu (dung dịch trong suốt)';
  let universalColorHex = '#22c55e'; // Green at pH 7

  if (ph < 3.0) {
    classification = 'STRONG_ACID';
    classificationVi = 'Môi trường Axit mạnh';
    litmusColorHex = '#ef4444'; // Red
    litmusDescriptionVi = 'Giấy quỳ tím hóa đỏ đậm tức thì';
    phenolphthaleinColorHex = '#f8fafc';
    phenolphthaleinDescriptionVi = 'Không đổi màu (trong suốt không màu)';
    universalColorHex = '#dc2626'; // Deep red
  } else if (ph < 6.5) {
    classification = 'WEAK_ACID';
    classificationVi = 'Môi trường Axit yếu';
    litmusColorHex = '#f87171'; // Light red / pink
    litmusDescriptionVi = 'Giấy quỳ tím chuyển sang màu hồng đỏ nhạt';
    phenolphthaleinColorHex = '#f8fafc';
    phenolphthaleinDescriptionVi = 'Không đổi màu';
    universalColorHex = '#f97316'; // Orange
  } else if (ph > 11.0) {
    classification = 'STRONG_BASE';
    classificationVi = 'Môi trường Kiềm/Bazơ mạnh';
    litmusColorHex = '#2563eb'; // Deep blue
    litmusDescriptionVi = 'Giấy quỳ tím hóa xanh lam thẫm';
    phenolphthaleinColorHex = '#ec4899'; // Vibrant magenta / hot pink
    phenolphthaleinDescriptionVi = 'Hóa hồng cánh sen / đỏ thẫm rực rỡ';
    universalColorHex = '#6366f1'; // Violet/purple
  } else if (ph > 7.5) {
    classification = 'WEAK_BASE';
    classificationVi = 'Môi trường Kiềm/Bazơ yếu';
    litmusColorHex = '#38bdf8'; // Light blue
    litmusDescriptionVi = 'Giấy quỳ tím chuyển sang màu xanh lam nhạt';
    phenolphthaleinColorHex = ph >= 8.3 ? '#f472b6' : '#f8fafc';
    phenolphthaleinDescriptionVi = ph >= 8.3 ? 'Hóa hồng nhạt' : 'Không màu (pH < 8.3)';
    universalColorHex = '#0ea5e9'; // Cyan
  }

  return {
    phValue: ph,
    pOhValue: pOh,
    hPlusMolarity: hPlus,
    ohMinusMolarity: ohMinus,
    classification,
    classificationVi,
    litmusColorHex,
    litmusDescriptionVi,
    phenolphthaleinColorHex,
    phenolphthaleinDescriptionVi,
    universalColorHex,
    explanationVi: explanation,
  };
}
