import React, { useState } from 'react';
import { Substance, ReactionOutcome, ExperimentLog, BktSkillState } from '../types';
import { findReactionOutcome } from '../engine/chemicalEngine';
import { ALL_SUBSTANCES } from '../data/substances';
import { MacroLabView } from '../components/MacroLabView';
import { MicroZoomView } from '../components/MicroZoomView';
import { ReagentShelf } from '../components/ReagentShelf';
import { PedagogicalStepper } from '../components/PedagogicalStepper';
import { RecentExperimentsDrawer } from '../components/RecentExperimentsDrawer';
import { PeriodicTableBottomSheet } from '../components/PeriodicTableBottomSheet';
import { PhIndicatorModal } from '../components/PhIndicatorModal';
import { StoichiometryCalculatorModal } from '../components/StoichiometryCalculatorModal';
import { KineticsEnthalpyModal } from '../components/KineticsEnthalpyModal';
import { SafetyTipBanner } from '../components/SafetyTipBanner';
import { generatePdfReport } from '../utils/pdfReportGenerator';
import { Eye, Atom, Sparkles, TableProperties, FileDown, BatteryCharging, ShieldAlert, Gauge, Calculator, Droplets, Activity } from 'lucide-react';

interface LabScreenProps {
  reactantA: Substance | null;
  reactantB: Substance | null;
  onSelectA: (sub: Substance) => void;
  onSelectB: (sub: Substance) => void;
  onClearReactants: () => void;
  onRecordExperiment: (
    title: string,
    isCorrect: boolean,
    studentExplanation: string,
    aiFeedback: string,
    competencyId: string
  ) => void;
  onOpenSocratic: () => void;
  onOpenSocraticWithPrompt?: (prompt?: string) => void;
  isIupacMode: boolean;
  logs?: ExperimentLog[];
  skills?: BktSkillState[];
  onReloadExperiment?: (reactantAId?: string, reactantBId?: string) => void;
  onOpenGalvanic?: () => void;
  onOpenSafety?: () => void;
}

export const LabScreen: React.FC<LabScreenProps> = ({
  reactantA,
  reactantB,
  onSelectA,
  onSelectB,
  onClearReactants,
  onRecordExperiment,
  onOpenSocratic,
  onOpenSocraticWithPrompt,
  isIupacMode,
  logs = [],
  skills = [],
  onReloadExperiment,
  onOpenGalvanic,
  onOpenSafety,
}) => {
  const [viewMode, setViewMode] = useState<'MACRO' | 'MICRO'>('MACRO');
  const [timeScale, setTimeScale] = useState<number>(1.0);
  const [currentStep, setCurrentStep] = useState(1);
  const [isReactionActive, setIsReactionActive] = useState(false);
  const [isHeated, setIsHeated] = useState(false);
  const [isPeriodicTableOpen, setIsPeriodicTableOpen] = useState(false);
  const [isPhModalOpen, setIsPhModalOpen] = useState(false);
  const [isStoichModalOpen, setIsStoichModalOpen] = useState(false);
  const [isKineticsModalOpen, setIsKineticsModalOpen] = useState(false);

  // Compute reaction outcome
  const outcome: ReactionOutcome | null = findReactionOutcome(reactantA, reactantB);

  const handleActivateReaction = () => {
    setIsReactionActive(true);
  };

  const handleResetReaction = () => {
    setIsReactionActive(false);
    setIsHeated(false);
    setCurrentStep(1);
  };

  const handleClearAll = () => {
    handleResetReaction();
    onClearReactants();
  };

  const handleOpenSocraticHazard = (prompt?: string) => {
    if (onOpenSocraticWithPrompt) {
      onOpenSocraticWithPrompt(prompt);
    } else {
      onOpenSocratic();
    }
  };

  const handleExportLabPdf = () => {
    generatePdfReport({
      sessionId: 'LAB-SESSION-' + new Date().getHours() + new Date().getMinutes(),
      sessionDuration: '30 phút',
      logs,
      skills,
      safetyScore: logs.some((l) => l.hazardType && l.hazardType !== 'NONE') ? 85 : 98,
    });
  };

  const handleSelectFromPeriodicTable = (reagentId: string) => {
    const found = ALL_SUBSTANCES.find((s) => s.id === reagentId);
    if (!found) return;
    if (!reactantA) {
      onSelectA(found);
    } else if (!reactantB) {
      onSelectB(found);
    } else {
      onSelectA(found);
    }
  };

  return (
    <div className="flex flex-col gap-5 p-4 sm:p-6 max-w-6xl mx-auto relative">
      {/* Safety Tip of the Day Notification Banner */}
      {onOpenSafety && (
        <SafetyTipBanner
          onOpenSafetyModal={onOpenSafety}
          onAskSocratic={handleOpenSocraticHazard}
        />
      )}

      {/* Visual Simulation & Apparatus Stage */}
      <div className="flex flex-col gap-3">
        {/* Toggle View Mode Bar (Vĩ mô vs Vi mô) & Quick Action Tools */}
        <div className="flex items-center justify-between flex-wrap gap-2">
          <div className="flex flex-wrap items-center gap-2">
            <div className="flex items-center gap-1.5 p-1 bg-slate-900 border border-slate-800 rounded-xl">
              <button
                onClick={() => setViewMode('MACRO')}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                  viewMode === 'MACRO'
                    ? 'bg-cyan-500 text-slate-950 shadow-md shadow-cyan-500/20'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                <Eye className="w-3.5 h-3.5" />
                <span>Quan sát Vĩ mô</span>
              </button>
              <button
                onClick={() => setViewMode('MICRO')}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                  viewMode === 'MICRO'
                    ? 'bg-cyan-500 text-slate-950 shadow-md shadow-cyan-500/20'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                <Atom className="w-3.5 h-3.5" />
                <span>Phóng to Vi mô (10.000.000×)</span>
              </button>
            </div>

            {/* Simulation Time-Scale Control (0.5x, 1x, 2x) */}
            <div
              className="flex items-center bg-slate-900 p-1 rounded-xl border border-slate-800"
              title="Tốc độ mô phỏng phản ứng: 0.5x (chuyển động chậm), 1x (thực tế), 2x (nhanh)"
            >
              <div className="flex items-center gap-1 px-2 text-[11px] font-medium text-slate-400">
                <Gauge className="w-3.5 h-3.5 text-cyan-400" />
                <span className="hidden sm:inline">Tốc độ:</span>
              </div>
              {([0.5, 1.0, 2.0] as const).map((scale) => (
                <button
                  key={scale}
                  type="button"
                  onClick={() => setTimeScale(scale)}
                  className={`px-2.5 py-1 rounded-lg text-xs font-mono font-bold transition-all ${
                    timeScale === scale
                      ? 'bg-cyan-500 text-slate-950 shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`}
                  title={
                    scale === 0.5
                      ? '0.5x - Làm chậm phản ứng để quan sát chi tiết hiện tượng và ion/electron'
                      : scale === 1.0
                      ? '1x - Tốc độ thời gian thực'
                      : '2x - Tăng tốc độ mô phỏng'
                  }
                >
                  {scale}x
                </button>
              ))}
            </div>
          </div>

          {/* Action Tools: Periodic Table, PDF Report, Socratic AI */}
          <div className="flex items-center gap-2 flex-wrap">
            {/* Stoichiometry & Standard Gas Law Calculator */}
            <button
              onClick={() => setIsStoichModalOpen(true)}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-cyan-500/30 text-cyan-300 font-bold text-xs transition-all shadow-sm cursor-pointer"
              title="Tính chất hết/dư, thể tích khí chuẩn ĐKC 24.79L và lượng kết tủa GDPT 2018"
            >
              <Calculator className="w-3.5 h-3.5 text-cyan-400" />
              <span>Tính lượng chất (24.79L)</span>
            </button>

            {/* pH & Color Indicators Modal Button */}
            <button
              onClick={() => setIsPhModalOpen(true)}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-cyan-500/30 text-cyan-300 font-bold text-xs transition-all shadow-sm cursor-pointer"
              title="Đo pH dung dịch & thử màu giấy quỳ tím, phenolphtalein"
            >
              <Droplets className="w-3.5 h-3.5 text-cyan-400" />
              <span>Đo pH & Quỳ tím</span>
            </button>

            {/* Reaction Kinetics & Activation Energy (Ea) Button */}
            <button
              onClick={() => setIsKineticsModalOpen(true)}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-amber-500/30 text-amber-300 font-bold text-xs transition-all shadow-sm cursor-pointer"
              title="Động học phản ứng, Năng lượng hoạt hóa Ea và Phân bố Maxwell-Boltzmann"
            >
              <Activity className="w-3.5 h-3.5 text-amber-400" />
              <span>Động học Ea</span>
            </button>

            {/* Galvanic Cell Simulator Button */}
            {onOpenGalvanic && (
              <button
                onClick={onOpenGalvanic}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-cyan-500/30 text-cyan-300 font-bold text-xs transition-all shadow-sm cursor-pointer"
                title="Mô phỏng Pin Galvanic và Dãy thế điện cực chuẩn E° (Hóa 12)"
              >
                <BatteryCharging className="w-3.5 h-3.5 text-cyan-400" />
                <span>Pin Galvanic</span>
              </button>
            )}

            {/* Safety Protocol Guide Button */}
            {onOpenSafety && (
              <button
                onClick={onOpenSafety}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-amber-500/30 text-amber-300 font-bold text-xs transition-all shadow-sm cursor-pointer"
                title="Cẩm nang an toàn hóa chất và sơ cứu khẩn cấp GDPT 2018"
              >
                <ShieldAlert className="w-3.5 h-3.5 text-amber-400" />
                <span>An toàn PTN</span>
              </button>
            )}

            {/* Interactive Periodic Table Button */}
            <button
              onClick={() => setIsPeriodicTableOpen(true)}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-cyan-500/30 text-cyan-300 font-bold text-xs transition-all shadow-sm cursor-pointer"
              title="Tra cứu Bảng tuần hoàn 20 nguyên tố đầu và kim loại chuyển tiếp"
            >
              <TableProperties className="w-3.5 h-3.5 text-cyan-400" />
              <span>Bảng tuần hoàn</span>
            </button>

            {/* Socratic Assistant Button */}
            <button
              onClick={onOpenSocratic}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-gradient-to-r from-amber-500/20 to-cyan-500/20 hover:from-amber-500/30 hover:to-cyan-500/30 border border-cyan-500/40 text-cyan-300 font-bold text-xs transition-all shadow-md cursor-pointer"
            >
              <Sparkles className="w-3.5 h-3.5 text-amber-400" />
              <span>Trợ lý Socratic AI</span>
            </button>
          </div>
        </div>

        {/* Dynamic Canvas Simulation (Macro or Micro) */}
        {viewMode === 'MACRO' ? (
          <MacroLabView
            reactantA={reactantA}
            reactantB={reactantB}
            outcome={outcome}
            isReactionActive={isReactionActive}
            isHeated={isHeated}
            onToggleHeated={() => setIsHeated(!isHeated)}
            onOpenSocratic={handleOpenSocraticHazard}
            timeScale={timeScale}
            onOpenPhModal={() => setIsPhModalOpen(true)}
            onOpenKineticsModal={() => setIsKineticsModalOpen(true)}
          />
        ) : (
          <MicroZoomView
            reactantA={reactantA}
            reactantB={reactantB}
            outcome={outcome}
            isReactionActive={isReactionActive}
            timeScale={timeScale}
          />
        )}
      </div>

      {/* 5 Most Recent Experiments Collapsible Drawer Component */}
      <RecentExperimentsDrawer
        logs={logs}
        onReloadExperiment={onReloadExperiment}
        onExportPdf={handleExportLabPdf}
      />

      {/* 4-Step Pedagogical Inquiry Stepper */}
      <PedagogicalStepper
        currentStep={currentStep}
        onSetStep={setCurrentStep}
        reactantA={reactantA}
        reactantB={reactantB}
        outcome={outcome}
        isReactionActive={isReactionActive}
        onActivateReaction={handleActivateReaction}
        onResetReaction={handleResetReaction}
        onRecordExperiment={onRecordExperiment}
        onOpenSocratic={onOpenSocratic}
      />

      {/* Chemical Reagent Shelf */}
      <ReagentShelf
        reactantA={reactantA}
        reactantB={reactantB}
        onSelectA={onSelectA}
        onSelectB={onSelectB}
        onClear={handleClearAll}
        isIupacMode={isIupacMode}
      />

      {/* Periodic Table Bottom Sheet Modal */}
      <PeriodicTableBottomSheet
        isOpen={isPeriodicTableOpen}
        onClose={() => setIsPeriodicTableOpen(false)}
        onSelectReagent={handleSelectFromPeriodicTable}
      />

      {/* pH Indicator & Acid-Base Spectrum Modal */}
      <PhIndicatorModal
        isOpen={isPhModalOpen}
        onClose={() => setIsPhModalOpen(false)}
        reactantA={reactantA}
        reactantB={reactantB}
        outcome={outcome}
        isReactionActive={isReactionActive}
        onOpenSocratic={onOpenSocraticWithPrompt}
      />

      {/* Stoichiometry & Standard Gas Law (24.79L) Modal */}
      <StoichiometryCalculatorModal
        isOpen={isStoichModalOpen}
        onClose={() => setIsStoichModalOpen(false)}
        initialReactantA={reactantA}
        initialReactantB={reactantB}
        initialOutcome={outcome}
        onOpenSocratic={onOpenSocraticWithPrompt}
      />

      {/* Kinetics & Activation Energy (Ea) Modal */}
      <KineticsEnthalpyModal
        isOpen={isKineticsModalOpen}
        onClose={() => setIsKineticsModalOpen(false)}
        outcome={outcome}
        isHeated={isHeated}
        onToggleHeated={() => setIsHeated(!isHeated)}
        onOpenSocratic={onOpenSocraticWithPrompt}
      />
    </div>
  );
};
