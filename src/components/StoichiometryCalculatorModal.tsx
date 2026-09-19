import React, { useState } from 'react';
import {
  X,
  Calculator,
  Scale,
  Sparkles,
  HelpCircle,
  CheckCircle2,
  Info,
  TrendingDown,
  Flame,
  Wind,
  Layers,
  FlaskConical,
} from 'lucide-react';
import { Substance, ReactionOutcome } from '../types';
import { KNOWN_REACTIONS } from '../engine/chemicalEngine';
import { ALL_SUBSTANCES } from '../data/substances';

interface StoichiometryCalculatorModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialReactantA?: Substance | null;
  initialReactantB?: Substance | null;
  initialOutcome?: ReactionOutcome | null;
  onOpenSocratic?: (prompt: string) => void;
}

interface StoichReactionConfig {
  id: string;
  name: string;
  equation: string;
  reactantAId: string;
  reactantAMolarMass: number; // g/mol
  reactantACoeff: number;
  reactantAUnit: 'GRAM' | 'SOLUTION';
  reactantBId: string;
  reactantBMolarMass: number; // g/mol
  reactantBCoeff: number;
  reactantBUnit: 'GRAM' | 'SOLUTION';
  gasFormula?: string;
  gasCoeff?: number;
  precipitateFormula?: string;
  precipitateMolarMass?: number;
  precipitateCoeff?: number;
  deltaH: number; // kJ/mol
}

const STOICH_REACTIONS: StoichReactionConfig[] = [
  {
    id: 'zn_hcl',
    name: 'Kẽm + Axit Clohidric',
    equation: 'Zn(s) + 2HCl(aq) → ZnCl2(aq) + H2(g)↑',
    reactantAId: 'zn',
    reactantAMolarMass: 65.38,
    reactantACoeff: 1,
    reactantAUnit: 'GRAM',
    reactantBId: 'hcl',
    reactantBMolarMass: 36.46,
    reactantBCoeff: 2,
    reactantBUnit: 'SOLUTION',
    gasFormula: 'H2',
    gasCoeff: 1,
    deltaH: -152.4,
  },
  {
    id: 'na_h2o',
    name: 'Natri + Nước',
    equation: '2Na(s) + 2H2O(l) → 2NaOH(aq) + H2(g)↑',
    reactantAId: 'na',
    reactantAMolarMass: 22.99,
    reactantACoeff: 2,
    reactantAUnit: 'GRAM',
    reactantBId: 'h2o',
    reactantBMolarMass: 18.02,
    reactantBCoeff: 2,
    reactantBUnit: 'GRAM',
    gasFormula: 'H2',
    gasCoeff: 1,
    deltaH: -368.6,
  },
  {
    id: 'bacl2_h2so4',
    name: 'Bari clorua + Axit sunfuric',
    equation: 'BaCl2(aq) + H2SO4(aq) → BaSO4(s)↓ + 2HCl(aq)',
    reactantAId: 'bacl2',
    reactantAMolarMass: 208.23,
    reactantACoeff: 1,
    reactantAUnit: 'SOLUTION',
    reactantBId: 'h2so4',
    reactantBMolarMass: 98.08,
    reactantBCoeff: 1,
    reactantBUnit: 'SOLUTION',
    precipitateFormula: 'BaSO4',
    precipitateMolarMass: 233.39,
    precipitateCoeff: 1,
    deltaH: -26.0,
  },
  {
    id: 'naoh_hcl',
    name: 'Natri hidroxit + Axit clohidric (Trung hòa)',
    equation: 'NaOH(aq) + HCl(aq) → NaCl(aq) + H2O(l)',
    reactantAId: 'naoh',
    reactantAMolarMass: 40.0,
    reactantACoeff: 1,
    reactantAUnit: 'SOLUTION',
    reactantBId: 'hcl',
    reactantBMolarMass: 36.46,
    reactantBCoeff: 1,
    reactantBUnit: 'SOLUTION',
    deltaH: -57.3,
  },
  {
    id: 'fe_cuso4',
    name: 'Sắt + Đồng(II) sunfat',
    equation: 'Fe(s) + CuSO4(aq) → FeSO4(aq) + Cu(s)↓',
    reactantAId: 'fe',
    reactantAMolarMass: 55.85,
    reactantACoeff: 1,
    reactantAUnit: 'GRAM',
    reactantBId: 'cuso4',
    reactantBMolarMass: 159.61,
    reactantBCoeff: 1,
    reactantBUnit: 'SOLUTION',
    precipitateFormula: 'Cu',
    precipitateMolarMass: 63.55,
    precipitateCoeff: 1,
    deltaH: -152.0,
  },
];

export const StoichiometryCalculatorModal: React.FC<StoichiometryCalculatorModalProps> = ({
  isOpen,
  onClose,
  initialReactantA,
  initialReactantB,
  onOpenSocratic,
}) => {
  // Find default reaction matching current laboratory reactants
  const matchedDefault = STOICH_REACTIONS.find(
    (r) =>
      (r.reactantAId === initialReactantA?.id && r.reactantBId === initialReactantB?.id) ||
      (r.reactantAId === initialReactantB?.id && r.reactantBId === initialReactantA?.id)
  );

  const [selectedReactionId, setSelectedReactionId] = useState<string>(
    matchedDefault ? matchedDefault.id : 'zn_hcl'
  );

  // Input states for Reactant A
  const [massA, setMassA] = useState<number>(6.54); // default ~0.1 mol Zn
  const [volA, setVolA] = useState<number>(100); // mL
  const [concA, setConcA] = useState<number>(1.0); // M

  // Input states for Reactant B
  const [massB, setMassB] = useState<number>(18.0); // g
  const [volB, setVolB] = useState<number>(150); // mL
  const [concB, setConcB] = useState<number>(1.0); // M (e.g. HCl 1M)

  // Interactive Quiz state
  const [quizAnswer, setQuizAnswer] = useState<string>('');
  const [quizFeedback, setQuizFeedback] = useState<string | null>(null);

  if (!isOpen) return null;

  const currentRxn = STOICH_REACTIONS.find((r) => r.id === selectedReactionId) || STOICH_REACTIONS[0];
  const subA = ALL_SUBSTANCES.find((s) => s.id === currentRxn.reactantAId);
  const subB = ALL_SUBSTANCES.find((s) => s.id === currentRxn.reactantBId);

  // Calculate moles of Reactant A
  const molA =
    currentRxn.reactantAUnit === 'GRAM'
      ? +(massA / currentRxn.reactantAMolarMass).toFixed(4)
      : +((volA * concA) / 1000).toFixed(4);

  // Calculate moles of Reactant B
  const molB =
    currentRxn.reactantBUnit === 'GRAM'
      ? +(massB / currentRxn.reactantBMolarMass).toFixed(4)
      : +((volB * concB) / 1000).toFixed(4);

  // Determine Limiting Reactant
  // Ratio = mol / coefficient
  const ratioA = molA / currentRxn.reactantACoeff;
  const ratioB = molB / currentRxn.reactantBCoeff;

  const isALimiting = ratioA <= ratioB;
  const limitingReactantName = isALimiting
    ? subA?.formula || 'Chất A'
    : subB?.formula || 'Chất B';
  const excessReactantName = isALimiting
    ? subB?.formula || 'Chất B'
    : subA?.formula || 'Chất A';

  const rxnMoles = Math.min(ratioA, ratioB); // Extent of reaction in terms of base stoichiometry

  // Reacted amounts
  const molAReacted = +(rxnMoles * currentRxn.reactantACoeff).toFixed(4);
  const molBReacted = +(rxnMoles * currentRxn.reactantBCoeff).toFixed(4);
  const molAExcess = +(molA - molAReacted).toFixed(4);
  const molBExcess = +(molB - molBReacted).toFixed(4);

  // Product Calculations:
  // 1. Gas volume at standard conditions (GDPT 2018: 25°C, 1 bar -> V = n * 24.79 L)
  const gasMoles = currentRxn.gasCoeff ? +(rxnMoles * currentRxn.gasCoeff).toFixed(4) : 0;
  const gasVolumeStandard2479 = +(gasMoles * 24.79).toFixed(3); // Liters
  const gasVolumeOld224 = +(gasMoles * 22.4).toFixed(3); // For pedagogical comparison

  // 2. Precipitate mass
  const precipMoles = currentRxn.precipitateCoeff ? +(rxnMoles * currentRxn.precipitateCoeff).toFixed(4) : 0;
  const precipMass = currentRxn.precipitateMolarMass
    ? +(precipMoles * currentRxn.precipitateMolarMass).toFixed(2)
    : 0;

  // 3. Heat released / absorbed: Q = rxnMoles * |deltaH|
  const heatExchangedKj = +(rxnMoles * Math.abs(currentRxn.deltaH)).toFixed(2);

  const handleAskSocraticCalculation = () => {
    const prompt = `Em muốn hiểu sâu về cách tìm chất phản ứng hết/dư trong phản ứng ${currentRxn.equation}, và tại sao chương trình GDPT 2018 lại dùng thể tích mol khí là 24.79 L/mol ở 25°C 1 bar thay vì 22.4 L/mol?`;
    if (onOpenSocratic) {
      onOpenSocratic(prompt);
      onClose();
    }
  };

  const handleCheckQuiz = () => {
    const numericAns = parseFloat(quizAnswer.replace(',', '.'));
    if (isNaN(numericAns)) {
      setQuizFeedback('Vui lòng nhập một con số hợp lệ.');
      return;
    }

    // Expected question: Thể tích khí sinh ra (L) theo chuẩn GDPT 2018
    const diff = Math.abs(numericAns - gasVolumeStandard2479);
    if (diff < 0.05) {
      setQuizFeedback(`✓ CHÍNH XÁC TUYỆT ĐỐI! V = ${gasMoles} mol × 24.79 L/mol = ${gasVolumeStandard2479} L.`);
    } else if (Math.abs(numericAns - gasVolumeOld224) < 0.05) {
      setQuizFeedback(`⚠️ LƯU Ý GDPT 2018: Bạn đã tính theo chuẩn cũ (22.4 L/mol ở 0°C). Chuẩn mới 2018 (25°C, 1 bar) là 24.79 L/mol ⇒ V = ${gasVolumeStandard2479} L!`);
    } else {
      setQuizFeedback(`❌ Chưa đúng. Gợi ý: Chất phản ứng hết là ${limitingReactantName}, số mol khí = ${gasMoles} mol ⇒ V = ${gasMoles} × 24.79 = ${gasVolumeStandard2479} L.`);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="relative w-full max-w-4xl max-h-[92vh] overflow-y-auto bg-[#0a1324] border border-cyan-500/30 rounded-2xl shadow-2xl flex flex-col text-slate-100">
        {/* Header */}
        <div className="sticky top-0 z-20 flex items-center justify-between p-4 sm:p-5 bg-[#0b162c]/95 border-b border-cyan-500/20 backdrop-blur-md">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-gradient-to-tr from-cyan-500 to-emerald-400 text-slate-950 shadow-lg shadow-cyan-500/25">
              <Calculator className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base sm:text-lg font-bold text-white tracking-tight">
                  Máy Tính Lượng Chất & Khí Chuẩn ĐKC 24.79L
                </h2>
                <span className="text-[10px] font-mono font-bold px-2 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-500/40">
                  Chuẩn Hóa GDPT 2018
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Tính chất hết / chất dư • Thể tích khí V = n × 24.79 L (25°C, 1 bar) • Kết tủa & Nhiệt Enthalpy
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
            title="Đóng cửa sổ"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-4 sm:p-6 space-y-6">
          {/* Reaction Selector */}
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-300 flex items-center gap-1.5">
              <FlaskConical className="w-4 h-4 text-cyan-400" />
              <span>Chọn Phản Ứng Hóa Học Cần Tính Toán:</span>
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2">
              {STOICH_REACTIONS.map((rxn) => (
                <button
                  key={rxn.id}
                  onClick={() => setSelectedReactionId(rxn.id)}
                  className={`p-2.5 rounded-xl text-left border transition-all text-xs ${
                    selectedReactionId === rxn.id
                      ? 'bg-cyan-950/80 border-cyan-400 text-cyan-200 font-bold shadow-md shadow-cyan-500/10'
                      : 'bg-slate-900/70 border-slate-800 text-slate-300 hover:border-slate-700'
                  }`}
                >
                  <div className="font-semibold text-white">{rxn.name}</div>
                  <div className="font-mono text-[11px] text-cyan-400/90 mt-0.5 truncate">
                    {rxn.equation}
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* Balanced Equation Highlight */}
          <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
            <div>
              <div className="text-[10px] font-mono uppercase text-slate-400 font-bold">
                Phương Trình Hóa Học Đã Cân Bằng:
              </div>
              <div className="text-sm sm:text-base font-mono font-bold text-cyan-300 mt-0.5">
                {currentRxn.equation}
              </div>
            </div>
            <div className="text-xs font-mono font-semibold px-2.5 py-1 rounded bg-[#070e1c] border border-slate-800 text-amber-300">
              ΔrH°298 = {currentRxn.deltaH} kJ/mol
            </div>
          </div>

          {/* Interactive Reactants Input Stage */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Reactant A Card */}
            <div className="bg-slate-900/90 rounded-2xl p-4 border border-slate-800 space-y-3">
              <div className="flex items-center justify-between">
                <span className="font-bold text-xs text-cyan-300">
                  Chất phản ứng A: {subA?.formula || 'Chất A'}
                </span>
                <span className="text-[10px] font-mono text-slate-400">
                  M = {currentRxn.reactantAMolarMass} g/mol
                </span>
              </div>

              {currentRxn.reactantAUnit === 'GRAM' ? (
                <div>
                  <div className="flex justify-between text-xs mb-1">
                    <span className="text-slate-400">Khối lượng (m):</span>
                    <span className="font-mono font-bold text-white">{massA.toFixed(2)} gam</span>
                  </div>
                  <input
                    type="range"
                    min="0.1"
                    max="30"
                    step="0.1"
                    value={massA}
                    onChange={(e) => setMassA(parseFloat(e.target.value))}
                    className="w-full accent-cyan-400 cursor-pointer"
                  />
                  <div className="mt-2 flex items-center justify-between text-[11px] font-mono text-slate-400">
                    <span>Số mol nA = m / M:</span>
                    <span className="text-cyan-400 font-bold">{molA} mol</span>
                  </div>
                </div>
              ) : (
                <div className="space-y-2">
                  <div>
                    <div className="flex justify-between text-xs mb-1">
                      <span className="text-slate-400">Thể tích dung dịch (V):</span>
                      <span className="font-mono font-bold text-white">{volA} mL</span>
                    </div>
                    <input
                      type="range"
                      min="10"
                      max="500"
                      step="10"
                      value={volA}
                      onChange={(e) => setVolA(parseFloat(e.target.value))}
                      className="w-full accent-cyan-400 cursor-pointer"
                    />
                  </div>
                  <div>
                    <div className="flex justify-between text-xs mb-1">
                      <span className="text-slate-400">Nồng độ mol (CM):</span>
                      <span className="font-mono font-bold text-white">{concA.toFixed(1)} M</span>
                    </div>
                    <input
                      type="range"
                      min="0.1"
                      max="3.0"
                      step="0.1"
                      value={concA}
                      onChange={(e) => setConcA(parseFloat(e.target.value))}
                      className="w-full accent-cyan-400 cursor-pointer"
                    />
                  </div>
                  <div className="flex items-center justify-between text-[11px] font-mono text-slate-400 pt-1 border-t border-slate-800">
                    <span>Số mol nA = CM × V:</span>
                    <span className="text-cyan-400 font-bold">{molA} mol</span>
                  </div>
                </div>
              )}
            </div>

            {/* Reactant B Card */}
            <div className="bg-slate-900/90 rounded-2xl p-4 border border-slate-800 space-y-3">
              <div className="flex items-center justify-between">
                <span className="font-bold text-xs text-cyan-300">
                  Chất phản ứng B: {subB?.formula || 'Chất B'}
                </span>
                <span className="text-[10px] font-mono text-slate-400">
                  M = {currentRxn.reactantBMolarMass} g/mol
                </span>
              </div>

              {currentRxn.reactantBUnit === 'GRAM' ? (
                <div>
                  <div className="flex justify-between text-xs mb-1">
                    <span className="text-slate-400">Khối lượng (m):</span>
                    <span className="font-mono font-bold text-white">{massB.toFixed(2)} gam</span>
                  </div>
                  <input
                    type="range"
                    min="1"
                    max="100"
                    step="1"
                    value={massB}
                    onChange={(e) => setMassB(parseFloat(e.target.value))}
                    className="w-full accent-cyan-400 cursor-pointer"
                  />
                  <div className="mt-2 flex items-center justify-between text-[11px] font-mono text-slate-400">
                    <span>Số mol nB = m / M:</span>
                    <span className="text-cyan-400 font-bold">{molB} mol</span>
                  </div>
                </div>
              ) : (
                <div className="space-y-2">
                  <div>
                    <div className="flex justify-between text-xs mb-1">
                      <span className="text-slate-400">Thể tích dung dịch (V):</span>
                      <span className="font-mono font-bold text-white">{volB} mL</span>
                    </div>
                    <input
                      type="range"
                      min="10"
                      max="500"
                      step="10"
                      value={volB}
                      onChange={(e) => setVolB(parseFloat(e.target.value))}
                      className="w-full accent-cyan-400 cursor-pointer"
                    />
                  </div>
                  <div>
                    <div className="flex justify-between text-xs mb-1">
                      <span className="text-slate-400">Nồng độ mol (CM):</span>
                      <span className="font-mono font-bold text-white">{concB.toFixed(1)} M</span>
                    </div>
                    <input
                      type="range"
                      min="0.1"
                      max="3.0"
                      step="0.1"
                      value={concB}
                      onChange={(e) => setConcB(parseFloat(e.target.value))}
                      className="w-full accent-cyan-400 cursor-pointer"
                    />
                  </div>
                  <div className="flex items-center justify-between text-[11px] font-mono text-slate-400 pt-1 border-t border-slate-800">
                    <span>Số mol nB = CM × V:</span>
                    <span className="text-cyan-400 font-bold">{molB} mol</span>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Stoichiometric Limiting / Excess Reactant Analysis Banner */}
          <div className="p-4 rounded-xl bg-[#081224] border border-cyan-500/30 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
            <div className="space-y-1">
              <div className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
                Xác Định Chất Phản Ứng Hết / Dư (So Sánh Tỉ Lệ n / Hệ Số):
              </div>
              <div className="text-xs sm:text-sm font-semibold flex items-center gap-2">
                <span className="px-2 py-0.5 rounded bg-emerald-950 text-emerald-300 border border-emerald-500/40 font-bold">
                  Chất phản ứng hết: {limitingReactantName}
                </span>
                <span className="px-2 py-0.5 rounded bg-amber-950 text-amber-300 border border-amber-500/40 font-bold">
                  Chất còn dư: {excessReactantName} ({isALimiting ? molBExcess : molAExcess} mol)
                </span>
              </div>
            </div>

            <div className="text-[11px] font-mono text-slate-400 bg-slate-900 p-2 rounded-lg border border-slate-800">
              Tỉ lệ A: ({molA}/{currentRxn.reactantACoeff} = {ratioA.toFixed(3)}){' '}
              {ratioA <= ratioB ? '≤' : '>'} Tỉ lệ B: ({molB}/{currentRxn.reactantBCoeff} ={' '}
              {ratioB.toFixed(3)})
            </div>
          </div>

          {/* Product Output Telemetry Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            {/* 1. Gas Volume Card */}
            {currentRxn.gasFormula ? (
              <div className="bg-slate-900/90 rounded-xl p-4 border border-cyan-500/30 space-y-2">
                <div className="flex items-center gap-2 text-cyan-400 font-bold text-xs">
                  <Wind className="w-4 h-4" />
                  <span>Thể Tích Khí {currentRxn.gasFormula} (ĐKC):</span>
                </div>
                <div className="text-2xl font-mono font-black text-white">
                  {gasVolumeStandard2479} <span className="text-sm font-normal text-slate-400">Lít</span>
                </div>
                <div className="text-[11px] font-mono text-slate-400 space-y-0.5 pt-1 border-t border-slate-800">
                  <div>Số mol khí: <span className="text-cyan-400 font-bold">{gasMoles} mol</span></div>
                  <div className="text-emerald-400 font-semibold">
                    V = n × 24.79 L (Chuẩn GDPT 2018)
                  </div>
                  <div className="text-[10px] text-slate-500 line-through">
                    (Chuẩn cũ 0°C: {gasVolumeOld224} L)
                  </div>
                </div>
              </div>
            ) : (
              <div className="bg-slate-900/40 rounded-xl p-4 border border-slate-800/80 space-y-1 text-slate-500 text-xs flex flex-col justify-center items-center text-center">
                <Wind className="w-4 h-4 opacity-40 mb-1" />
                <span>Không giải phóng chất khí</span>
              </div>
            )}

            {/* 2. Precipitate Mass Card */}
            {currentRxn.precipitateFormula ? (
              <div className="bg-slate-900/90 rounded-xl p-4 border border-cyan-500/30 space-y-2">
                <div className="flex items-center gap-2 text-cyan-400 font-bold text-xs">
                  <Layers className="w-4 h-4" />
                  <span>Khối Lượng Kết Tủa {currentRxn.precipitateFormula}:</span>
                </div>
                <div className="text-2xl font-mono font-black text-white">
                  {precipMass} <span className="text-sm font-normal text-slate-400">gam</span>
                </div>
                <div className="text-[11px] font-mono text-slate-400 space-y-0.5 pt-1 border-t border-slate-800">
                  <div>Số mol: <span className="text-cyan-400 font-bold">{precipMoles} mol</span></div>
                  <div>M = {currentRxn.precipitateMolarMass} g/mol</div>
                </div>
              </div>
            ) : (
              <div className="bg-slate-900/40 rounded-xl p-4 border border-slate-800/80 space-y-1 text-slate-500 text-xs flex flex-col justify-center items-center text-center">
                <Layers className="w-4 h-4 opacity-40 mb-1" />
                <span>Không tạo chất kết tủa</span>
              </div>
            )}

            {/* 3. Heat & Enthalpy Card */}
            <div className="bg-slate-900/90 rounded-xl p-4 border border-cyan-500/30 space-y-2">
              <div className="flex items-center gap-2 text-rose-400 font-bold text-xs">
                <Flame className="w-4 h-4" />
                <span>Nhiệt Lượng Phản Ứng (Q):</span>
              </div>
              <div className="text-2xl font-mono font-black text-rose-300">
                {heatExchangedKj} <span className="text-sm font-normal text-slate-400">kJ</span>
              </div>
              <div className="text-[11px] font-mono text-slate-400 space-y-0.5 pt-1 border-t border-slate-800">
                <div>Q = n × |ΔrH°298|</div>
                <div className="text-amber-300 font-semibold">
                  {currentRxn.deltaH < 0 ? 'Phản ứng Tỏa nhiệt (ΔH < 0)' : 'Phản ứng Thu nhiệt (ΔH > 0)'}
                </div>
              </div>
            </div>
          </div>

          {/* Pedagogical Callout: GDPT 2018 Standard Conditions (24.79 L/mol) */}
          <div className="p-3.5 rounded-xl bg-cyan-950/40 border border-cyan-500/30 flex items-start gap-3 text-xs">
            <Info className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
            <div className="text-slate-300 leading-relaxed">
              <span className="text-cyan-300 font-bold">Quy chuẩn GDPT 2018: </span>
              Điều kiện chuẩn (đkc) theo IUPAC hiện hành được định nghĩa tại nhiệt độ{' '}
              <span className="font-bold text-white">25°C (298.15 K)</span> và áp suất{' '}
              <span className="font-bold text-white">1 bar (10⁵ Pa)</span>. Tại điều kiện này, 1 mol chất khí bất kì chiếm thể tích là{' '}
              <span className="font-bold text-cyan-300">24.79 L</span> (thay thế cho quy chuẩn cũ 22.4 L ở 0°C, 1 atm).
            </div>
          </div>

          {/* Interactive Self-Practice Problem */}
          <div className="p-4 rounded-xl bg-gradient-to-r from-slate-900 to-[#0e1b34] border border-cyan-500/20 space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2 font-bold text-xs text-cyan-300">
                <HelpCircle className="w-4 h-4 text-cyan-400" />
                <span>Thực Hành Tính Nhanh Năng Lực Giải Toán Hóa Học:</span>
              </div>
              <span className="text-[10px] font-mono text-slate-400">Tự luyện trực tiếp</span>
            </div>

            <p className="text-xs text-slate-200">
              Với lượng hóa chất đã thiết lập ở trên, hãy tính thể tích khí {currentRxn.gasFormula || 'sản phẩm'} thu được ở điều kiện chuẩn 25°C, 1 bar (Lít)?
            </p>

            <div className="flex items-center gap-2">
              <input
                type="text"
                value={quizAnswer}
                onChange={(e) => setQuizAnswer(e.target.value)}
                placeholder={`Nhập số lít khí (ví dụ: ${gasVolumeStandard2479})`}
                className="flex-1 bg-slate-950 border border-slate-700 rounded-xl px-3 py-2 text-xs font-mono text-white placeholder-slate-500 focus:outline-hidden focus:border-cyan-400"
              />
              <button
                onClick={handleCheckQuiz}
                className="px-4 py-2 rounded-xl bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold text-xs transition-colors cursor-pointer"
              >
                Kiểm Tra
              </button>
            </div>

            {quizFeedback && (
              <div className="p-2.5 rounded-lg bg-slate-950/90 border border-slate-800 text-xs font-medium">
                {quizFeedback}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 bg-[#081224] border-t border-slate-800 flex items-center justify-between">
          <button
            onClick={handleAskSocraticCalculation}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-cyan-500/30 text-cyan-300 font-bold text-xs transition-colors cursor-pointer"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>Hỏi Socratic AI về Bản Chất Tỉ Lệ Mol</span>
          </button>

          <button
            onClick={onClose}
            className="px-5 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold transition-colors cursor-pointer"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};
