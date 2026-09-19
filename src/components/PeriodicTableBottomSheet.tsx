import React, { useState } from 'react';
import { X, Atom, Sparkles, Info, Check, Search, Layers, Flame, ShieldAlert } from 'lucide-react';

export interface PeriodicElement {
  z: number;
  symbol: string;
  nameEn: string;
  nameVi: string;
  mass: number;
  valences: string;
  state: 'SOLID' | 'LIQUID' | 'GAS';
  stateVi: string;
  category: 'alkali' | 'alkaline_earth' | 'transition' | 'post_transition' | 'metalloid' | 'nonmetal' | 'halogen' | 'noble_gas';
  categoryLabelVi: string;
  categoryColor: string; // Tailwind color token
  electronConfig: string;
  period: number;
  group: string;
  electronegativity?: number;
  labRelevanceVi: string;
  reagentId?: string; // If mapped to lab reagent
}

export const PERIODIC_ELEMENTS_SET: PeriodicElement[] = [
  // Period 1
  {
    z: 1,
    symbol: 'H',
    nameEn: 'Hydrogen',
    nameVi: 'Hydro',
    mass: 1.008,
    valences: 'I (+1, -1)',
    state: 'GAS',
    stateVi: 'Khí không màu',
    category: 'nonmetal',
    categoryLabelVi: 'Phi kim',
    categoryColor: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40',
    electronConfig: '1s¹',
    period: 1,
    group: 'IA',
    electronegativity: 2.20,
    labRelevanceVi: 'Sản phẩm khí thoát ra khi kim loại (Zn, Fe, Na) đẩy H+ từ axit loãng hoặc nước.',
    reagentId: 'h2o'
  },
  {
    z: 2,
    symbol: 'He',
    nameEn: 'Helium',
    nameVi: 'Heli',
    mass: 4.003,
    valences: '0 (Trơ)',
    state: 'GAS',
    stateVi: 'Khí hiếm',
    category: 'noble_gas',
    categoryLabelVi: 'Khí hiếm',
    categoryColor: 'bg-purple-500/20 text-purple-300 border-purple-500/40',
    electronConfig: '1s²',
    period: 1,
    group: 'VIIIA',
    labRelevanceVi: 'Khí trơ có lớp vỏ bão hòa 2e, không tham gia phản ứng hóa học thông thường.'
  },
  // Period 2
  {
    z: 3,
    symbol: 'Li',
    nameEn: 'Lithium',
    nameVi: 'Liti',
    mass: 6.94,
    valences: 'I (+1)',
    state: 'SOLID',
    stateVi: 'Kim loại rắn',
    category: 'alkali',
    categoryLabelVi: 'Kim loại kiềm',
    categoryColor: 'bg-rose-500/20 text-rose-300 border-rose-500/40',
    electronConfig: '[He] 2s¹',
    period: 2,
    group: 'IA',
    electronegativity: 0.98,
    labRelevanceVi: 'Kim loại nhẹ nhất, phản ứng với nước êm dịu hơn Natri, cho ngọn lửa đỏ thắm.'
  },
  {
    z: 4,
    symbol: 'Be',
    nameEn: 'Beryllium',
    nameVi: 'Beri',
    mass: 9.012,
    valences: 'II (+2)',
    state: 'SOLID',
    stateVi: 'Kim loại rắn',
    category: 'alkaline_earth',
    categoryLabelVi: 'Kim loại kiềm thổ',
    categoryColor: 'bg-amber-500/20 text-amber-300 border-amber-500/40',
    electronConfig: '[He] 2s²',
    period: 2,
    group: 'IIA',
    electronegativity: 1.57,
    labRelevanceVi: 'Hợp chất lưỡng tính, không tác dụng với nước ở nhiệt độ thường do có màng oxit bảo vệ.'
  },
  {
    z: 5,
    symbol: 'B',
    nameEn: 'Boron',
    nameVi: 'Bo',
    mass: 10.81,
    valences: 'III (+3)',
    state: 'SOLID',
    stateVi: 'Chất rắn á kim',
    category: 'metalloid',
    categoryLabelVi: 'Á kim',
    categoryColor: 'bg-teal-500/20 text-teal-300 border-teal-500/40',
    electronConfig: '[He] 2s² 2p¹',
    period: 2,
    group: 'IIIA',
    electronegativity: 2.04,
    labRelevanceVi: 'Cấu trúc mạng nguyên tử phức tạp, axit boric H3BO3 được dùng làm dung dịch rửa mắt sơ cứu.'
  },
  {
    z: 6,
    symbol: 'C',
    nameEn: 'Carbon',
    nameVi: 'Cacbon',
    mass: 12.011,
    valences: 'II, IV (-4, +2, +4)',
    state: 'SOLID',
    stateVi: 'Chất rắn phi kim',
    category: 'nonmetal',
    categoryLabelVi: 'Phi kim',
    categoryColor: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40',
    electronConfig: '[He] 2s² 2p²',
    period: 2,
    group: 'IVA',
    electronegativity: 2.55,
    labRelevanceVi: 'Nền tảng của Hóa học Hữu cơ. Dạng thù hình than hoạt tính có tính hấp phụ mạnh.'
  },
  {
    z: 7,
    symbol: 'N',
    nameEn: 'Nitrogen',
    nameVi: 'Nitơ',
    mass: 14.007,
    valences: 'I, II, III, IV, V (-3...+5)',
    state: 'GAS',
    stateVi: 'Khí không màu',
    category: 'nonmetal',
    categoryLabelVi: 'Phi kim',
    categoryColor: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40',
    electronConfig: '[He] 2s² 2p³',
    period: 2,
    group: 'VA',
    electronegativity: 3.04,
    labRelevanceVi: 'Có liên kết ba N≡N rất bền. Trong HNO3 đặc, N(+5) nhận e sinh khí độc NO2 màu nâu đỏ.',
    reagentId: 'hno3_conc'
  },
  {
    z: 8,
    symbol: 'O',
    nameEn: 'Oxygen',
    nameVi: 'Oxi',
    mass: 15.999,
    valences: 'II (-2)',
    state: 'GAS',
    stateVi: 'Khí không màu',
    category: 'nonmetal',
    categoryLabelVi: 'Phi kim',
    categoryColor: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40',
    electronConfig: '[He] 2s² 2p⁴',
    period: 2,
    group: 'VIA',
    electronegativity: 3.44,
    labRelevanceVi: 'Chất oxi hóa phổ biến nhất. Là thành phần của nước H2O, muối sunfat, bazơ kiềm.',
    reagentId: 'h2o'
  },
  {
    z: 9,
    symbol: 'F',
    nameEn: 'Fluorine',
    nameVi: 'Flo',
    mass: 18.998,
    valences: 'I (-1)',
    state: 'GAS',
    stateVi: 'Khí màu vàng lục nhạt',
    category: 'halogen',
    categoryLabelVi: 'Halogen',
    categoryColor: 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40',
    electronConfig: '[He] 2s² 2p⁵',
    period: 2,
    group: 'VIIA',
    electronegativity: 3.98,
    labRelevanceVi: 'Nguyên tố có độ âm điện lớn nhất bảng tuần hoàn, tính oxi hóa mạnh nhất, ăn mòn thủy tinh.'
  },
  {
    z: 10,
    symbol: 'Ne',
    nameEn: 'Neon',
    nameVi: 'Neon',
    mass: 20.180,
    valences: '0 (Trơ)',
    state: 'GAS',
    stateVi: 'Khí hiếm',
    category: 'noble_gas',
    categoryLabelVi: 'Khí hiếm',
    categoryColor: 'bg-purple-500/20 text-purple-300 border-purple-500/40',
    electronConfig: '[He] 2s² 2p⁶',
    period: 2,
    group: 'VIIIA',
    labRelevanceVi: 'Khí trơ phát ánh sáng đỏ cam rực rỡ trong ống phóng điện chân không.'
  },
  // Period 3
  {
    z: 11,
    symbol: 'Na',
    nameEn: 'Sodium',
    nameVi: 'Natri',
    mass: 22.990,
    valences: 'I (+1)',
    state: 'SOLID',
    stateVi: 'Kim loại kiềm mềm',
    category: 'alkali',
    categoryLabelVi: 'Kim loại kiềm',
    categoryColor: 'bg-rose-500/20 text-rose-300 border-rose-500/40',
    electronConfig: '[Ne] 3s¹',
    period: 3,
    group: 'IA',
    electronegativity: 0.93,
    labRelevanceVi: '⚠️ Kim loại kiềm gây nổ mãnh liệt với nước, nóng chảy thành viên bi và bốc cháy ngọn lửa vàng.',
    reagentId: 'na'
  },
  {
    z: 12,
    symbol: 'Mg',
    nameEn: 'Magnesium',
    nameVi: 'Magie',
    mass: 24.305,
    valences: 'II (+2)',
    state: 'SOLID',
    stateVi: 'Kim loại màu trắng bạc',
    category: 'alkaline_earth',
    categoryLabelVi: 'Kim loại kiềm thổ',
    categoryColor: 'bg-amber-500/20 text-amber-300 border-amber-500/40',
    electronConfig: '[Ne] 3s²',
    period: 3,
    group: 'IIA',
    electronegativity: 1.31,
    labRelevanceVi: 'Cháy trong không khí cho ánh sáng chói lòa, khử nước chậm ở nhiệt độ thường nhưng nhanh khi đun nóng.'
  },
  {
    z: 13,
    symbol: 'Al',
    nameEn: 'Aluminium',
    nameVi: 'Nhôm',
    mass: 26.982,
    valences: 'III (+3)',
    state: 'SOLID',
    stateVi: 'Kim loại dẻo',
    category: 'post_transition',
    categoryLabelVi: 'Kim loại',
    categoryColor: 'bg-blue-500/20 text-blue-300 border-blue-500/40',
    electronConfig: '[Ne] 3s² 3p¹',
    period: 3,
    group: 'IIIA',
    electronegativity: 1.61,
    labRelevanceVi: 'Kim loại có màng oxit Al2O3 bảo vệ bề mặt, hydroxit Al(OH)3 có tính chất lưỡng tính.'
  },
  {
    z: 14,
    symbol: 'Si',
    nameEn: 'Silicon',
    nameVi: 'Silic',
    mass: 28.085,
    valences: 'IV (+4, -4)',
    state: 'SOLID',
    stateVi: 'Chất bán dẫn',
    category: 'metalloid',
    categoryLabelVi: 'Á kim',
    categoryColor: 'bg-teal-500/20 text-teal-300 border-teal-500/40',
    electronConfig: '[Ne] 3s² 3p²',
    period: 3,
    group: 'IVA',
    electronegativity: 1.90,
    labRelevanceVi: 'Vật liệu chế tạo vi mạch bán dẫn, SiO2 là thành phần chính của cát và thủy tinh phòng thí nghiệm.'
  },
  {
    z: 15,
    symbol: 'P',
    nameEn: 'Phosphorus',
    nameVi: 'Photpho',
    mass: 30.974,
    valences: 'III, V (-3, +3, +5)',
    state: 'SOLID',
    stateVi: 'Chất rắn phi kim',
    category: 'nonmetal',
    categoryLabelVi: 'Phi kim',
    categoryColor: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40',
    electronConfig: '[Ne] 3s² 3p³',
    period: 3,
    group: 'VA',
    electronegativity: 2.19,
    labRelevanceVi: 'Có hai dạng thù hình chính: Photpho trắng (tự bốc cháy, cực độc) và Photpho đỏ (bền hơn).'
  },
  {
    z: 16,
    symbol: 'S',
    nameEn: 'Sulfur',
    nameVi: 'Lưu huỳnh',
    mass: 32.06,
    valences: 'II, IV, VI (-2, +4, +6)',
    state: 'SOLID',
    stateVi: 'Bột màu vàng tươi',
    category: 'nonmetal',
    categoryLabelVi: 'Phi kim',
    categoryColor: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40',
    electronConfig: '[Ne] 3s² 3p⁴',
    period: 3,
    group: 'VIA',
    electronegativity: 2.58,
    labRelevanceVi: 'Thành phần tạo axit H2SO4 đặc và kết tủa ion BaSO4 trắng đặc trưng.',
    reagentId: 'h2so4'
  },
  {
    z: 17,
    symbol: 'Cl',
    nameEn: 'Chlorine',
    nameVi: 'Clo',
    mass: 35.45,
    valences: 'I, III, V, VII (-1...+7)',
    state: 'GAS',
    stateVi: 'Khí màu vàng lục',
    category: 'halogen',
    categoryLabelVi: 'Halogen',
    categoryColor: 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40',
    electronConfig: '[Ne] 3s² 3p⁵',
    period: 3,
    group: 'VIIA',
    electronegativity: 3.16,
    labRelevanceVi: 'Khí clo mùi hắc, dung dịch HCl 1M là axit chuẩn để khảo sát phản ứng thế kim loại và trung hòa.',
    reagentId: 'hcl'
  },
  {
    z: 18,
    symbol: 'Ar',
    nameEn: 'Argon',
    nameVi: 'Agon',
    mass: 39.948,
    valences: '0 (Trơ)',
    state: 'GAS',
    stateVi: 'Khí hiếm',
    category: 'noble_gas',
    categoryLabelVi: 'Khí hiếm',
    categoryColor: 'bg-purple-500/20 text-purple-300 border-purple-500/40',
    electronConfig: '[Ne] 3s² 3p⁶',
    period: 3,
    group: 'VIIIA',
    labRelevanceVi: 'Khí trơ phổ biến thứ ba trong khí quyển Trái Đất, làm môi trường trơ trong hàn luyện kim.'
  },
  // Period 4
  {
    z: 19,
    symbol: 'K',
    nameEn: 'Potassium',
    nameVi: 'Kali',
    mass: 39.098,
    valences: 'I (+1)',
    state: 'SOLID',
    stateVi: 'Kim loại kiềm mềm',
    category: 'alkali',
    categoryLabelVi: 'Kim loại kiềm',
    categoryColor: 'bg-rose-500/20 text-rose-300 border-rose-500/40',
    electronConfig: '[Ar] 4s¹',
    period: 4,
    group: 'IA',
    electronegativity: 0.82,
    labRelevanceVi: 'Kim loại kiềm có tính khử mạnh hơn Natri, cháy với ngọn lửa màu tím hoa cà đặc trưng.'
  },
  {
    z: 20,
    symbol: 'Ca',
    nameEn: 'Calcium',
    nameVi: 'Canxi',
    mass: 40.078,
    valences: 'II (+2)',
    state: 'SOLID',
    stateVi: 'Kim loại màu xám bạc',
    category: 'alkaline_earth',
    categoryLabelVi: 'Kim loại kiềm thổ',
    categoryColor: 'bg-amber-500/20 text-amber-300 border-amber-500/40',
    electronConfig: '[Ar] 4s²',
    period: 4,
    group: 'IIA',
    electronegativity: 1.00,
    labRelevanceVi: 'Tác dụng chậm với nước ở nhiệt độ thường sinh Ca(OH)2 làm đục nước vôi trong bởi khí CO2.'
  },

  // Common Transition Metals requested: Fe, Cu, Zn, Ag, Ba
  {
    z: 26,
    symbol: 'Fe',
    nameEn: 'Iron',
    nameVi: 'Sắt',
    mass: 55.845,
    valences: 'II, III (+2, +3)',
    state: 'SOLID',
    stateVi: 'Kim loại rắn màu xám',
    category: 'transition',
    categoryLabelVi: 'Kim loại chuyển tiếp',
    categoryColor: 'bg-indigo-500/20 text-indigo-300 border-indigo-500/40',
    electronConfig: '[Ar] 3d⁶ 4s²',
    period: 4,
    group: 'VIIIB',
    electronegativity: 1.83,
    labRelevanceVi: 'Đẩy Cu ra khỏi dung dịch CuSO4; tác dụng với HCl sinh Fe²⁺ và giải phóng khí H2.',
    reagentId: 'fe'
  },
  {
    z: 29,
    symbol: 'Cu',
    nameEn: 'Copper',
    nameVi: 'Đồng',
    mass: 63.546,
    valences: 'I, II (+1, +2)',
    state: 'SOLID',
    stateVi: 'Kim loại màu đỏ ánh kim',
    category: 'transition',
    categoryLabelVi: 'Kim loại chuyển tiếp',
    categoryColor: 'bg-indigo-500/20 text-indigo-300 border-indigo-500/40',
    electronConfig: '[Ar] 3d¹⁰ 4s¹',
    period: 4,
    group: 'IB',
    electronegativity: 1.90,
    labRelevanceVi: 'Đứng sau H nên không phản ứng với HCl; tan trong HNO3 đặc giải phóng khí nâu đỏ NO2.',
    reagentId: 'cu'
  },
  {
    z: 30,
    symbol: 'Zn',
    nameEn: 'Zinc',
    nameVi: 'Kẽm',
    mass: 65.38,
    valences: 'II (+2)',
    state: 'SOLID',
    stateVi: 'Kim loại màu lam nhạt',
    category: 'transition',
    categoryLabelVi: 'Kim loại chuyển tiếp',
    categoryColor: 'bg-indigo-500/20 text-indigo-300 border-indigo-500/40',
    electronConfig: '[Ar] 3d¹⁰ 4s²',
    period: 4,
    group: 'IIB',
    electronegativity: 1.65,
    labRelevanceVi: 'Kim loại chuẩn để chứng minh thế điện cực E°(Zn²⁺/Zn) = -0.76V đẩy H+ từ HCl tạo khí H2 sủi bọt.',
    reagentId: 'zn'
  },
  {
    z: 47,
    symbol: 'Ag',
    nameEn: 'Silver',
    nameVi: 'Bạc',
    mass: 107.868,
    valences: 'I (+1)',
    state: 'SOLID',
    stateVi: 'Kim loại màu trắng sáng',
    category: 'transition',
    categoryLabelVi: 'Kim loại chuyển tiếp',
    categoryColor: 'bg-indigo-500/20 text-indigo-300 border-indigo-500/40',
    electronConfig: '[Kr] 4d¹⁰ 5s¹',
    period: 5,
    group: 'IB',
    electronegativity: 1.93,
    labRelevanceVi: 'Kim loại quý dẫn điện tốt nhất. Ion Ag+ dùng nhận biết ion halogenua (AgCl kết tủa trắng).'
  },
  {
    z: 56,
    symbol: 'Ba',
    nameEn: 'Barium',
    nameVi: 'Bari',
    mass: 137.327,
    valences: 'II (+2)',
    state: 'SOLID',
    stateVi: 'Kim loại kiềm thổ',
    category: 'alkaline_earth',
    categoryLabelVi: 'Kim loại kiềm thổ',
    categoryColor: 'bg-amber-500/20 text-amber-300 border-amber-500/40',
    electronConfig: '[Xe] 6s²',
    period: 6,
    group: 'IIA',
    electronegativity: 0.89,
    labRelevanceVi: 'Cation Ba²⁺ tạo kết tủa trắng BaSO4 đặc trưng không tan trong axit mạnh để nhận biết ion sunfat.',
    reagentId: 'bacl2'
  }
];

interface PeriodicTableBottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectReagent?: (reagentId: string) => void;
}

export const PeriodicTableBottomSheet: React.FC<PeriodicTableBottomSheetProps> = ({
  isOpen,
  onClose,
  onSelectReagent,
}) => {
  const [selectedElement, setSelectedElement] = useState<PeriodicElement>(
    PERIODIC_ELEMENTS_SET.find((e) => e.symbol === 'Zn') || PERIODIC_ELEMENTS_SET[0]
  );
  const [filterCategory, setFilterCategory] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState('');

  if (!isOpen) return null;

  const filteredElements = PERIODIC_ELEMENTS_SET.filter((el) => {
    const matchesCat =
      filterCategory === 'ALL' ||
      (filterCategory === 'METALS' &&
        ['alkali', 'alkaline_earth', 'transition', 'post_transition'].includes(el.category)) ||
      (filterCategory === 'NONMETALS' &&
        ['nonmetal', 'metalloid', 'halogen'].includes(el.category)) ||
      (filterCategory === 'NOBLE' && el.category === 'noble_gas');

    const matchesSearch =
      el.symbol.toLowerCase().includes(searchQuery.toLowerCase()) ||
      el.nameVi.toLowerCase().includes(searchQuery.toLowerCase()) ||
      el.nameEn.toLowerCase().includes(searchQuery.toLowerCase()) ||
      el.z.toString() === searchQuery.trim();

    return matchesCat && matchesSearch;
  });

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-slate-950/80 backdrop-blur-md animate-fadeIn">
      {/* Drawer Container */}
      <div className="relative w-full max-w-5xl bg-[#0a1526] border border-cyan-500/30 rounded-t-3xl sm:rounded-3xl shadow-2xl flex flex-col max-h-[90vh] sm:max-h-[85vh] overflow-hidden">
        {/* Header Bar */}
        <div className="flex items-center justify-between px-5 py-3.5 border-b border-slate-800 bg-[#070f1c]/90">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-cyan-500/20 border border-cyan-400/40 flex items-center justify-center">
              <Atom className="w-4 h-4 text-cyan-400" />
            </div>
            <div>
              <h3 className="font-extrabold text-sm sm:text-base text-white flex items-center gap-2">
                <span>BẢNG TUẦN HOÀN NGUYÊN TỐ HÓA HỌC GDPT 2018</span>
                <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-cyan-950 text-cyan-300 border border-cyan-500/40">
                  20 Nguyên tố đầu + Kim loại chuyển tiếp
                </span>
              </h3>
              <p className="text-[11px] text-slate-400">
                Tra cứu cấu hình electron, số oxi hóa, khối lượng mol và vai trò trong phản ứng lab
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Filter & Search Bar */}
        <div className="px-5 py-2.5 border-b border-slate-800/80 bg-slate-900/50 flex flex-wrap items-center justify-between gap-2.5">
          {/* Category Chips */}
          <div className="flex items-center gap-1.5 text-xs font-bold overflow-x-auto pb-1 sm:pb-0">
            {[
              { id: 'ALL', label: 'Tất cả (25)' },
              { id: 'METALS', label: 'Kim loại (13)' },
              { id: 'NONMETALS', label: 'Phi kim / Á kim (9)' },
              { id: 'NOBLE', label: 'Khí hiếm (3)' },
            ].map((cat) => (
              <button
                key={cat.id}
                onClick={() => setFilterCategory(cat.id)}
                className={`px-2.5 py-1 rounded-lg transition-all text-[11px] whitespace-nowrap ${
                  filterCategory === cat.id
                    ? 'bg-cyan-500 text-slate-950 font-extrabold shadow-sm'
                    : 'bg-slate-800/80 text-slate-300 hover:bg-slate-700'
                }`}
              >
                {cat.label}
              </button>
            ))}
          </div>

          {/* Quick Search */}
          <div className="relative flex items-center w-full sm:w-56">
            <Search className="w-3.5 h-3.5 absolute left-2.5 text-slate-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm theo KHHH, tên, Z..."
              className="w-full bg-slate-800/80 border border-slate-700 rounded-lg pl-8 pr-3 py-1 text-xs text-slate-200 placeholder-slate-500 focus:outline-none focus:border-cyan-400"
            />
          </div>
        </div>

        {/* Main Body: Grid + Selected Element Detail Card */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-5 flex flex-col lg:flex-row gap-5">
          {/* Elements Grid */}
          <div className="flex-1 flex flex-col gap-3">
            <div className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
              <Layers className="w-3.5 h-3.5 text-cyan-400" />
              <span>Lưới nguyên tố tương tác:</span>
            </div>

            <div className="grid grid-cols-4 sm:grid-cols-5 md:grid-cols-7 gap-2">
              {filteredElements.map((el) => {
                const isSelected = selectedElement.z === el.z;
                return (
                  <button
                    key={el.z}
                    onClick={() => setSelectedElement(el)}
                    className={`relative p-2 rounded-xl border flex flex-col items-center justify-between h-20 transition-all text-left group ${
                      isSelected
                        ? 'ring-2 ring-cyan-400 shadow-lg shadow-cyan-500/20 bg-slate-800 scale-105 z-10'
                        : 'hover:border-slate-500 hover:bg-slate-800/60 bg-slate-900/70'
                    } ${el.categoryColor}`}
                  >
                    {/* Top Row: Atomic number + Mass */}
                    <div className="w-full flex items-center justify-between text-[10px] font-mono text-slate-400">
                      <span className="font-bold">{el.z}</span>
                      <span>{el.mass.toFixed(1)}</span>
                    </div>

                    {/* Element Symbol */}
                    <div className="text-lg font-black text-white group-hover:scale-110 transition-transform">
                      {el.symbol}
                    </div>

                    {/* Vietnamese Name */}
                    <div className="text-[10px] text-slate-300 font-medium truncate w-full text-center">
                      {el.nameVi}
                    </div>
                  </button>
                );
              })}
            </div>

            {/* Legend */}
            <div className="flex flex-wrap items-center gap-3 pt-2 text-[11px] text-slate-400 border-t border-slate-800/80">
              <span className="font-bold text-slate-300">Chú giải màu sắc:</span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-rose-400"></span> Kim loại kiềm
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-amber-400"></span> Kiềm thổ
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-indigo-400"></span> Chuyển tiếp
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-400"></span> Phi kim
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-cyan-400"></span> Halogen
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-purple-400"></span> Khí hiếm
              </span>
            </div>
          </div>

          {/* Detailed Info Card for Selected Element */}
          <div className="w-full lg:w-80 shrink-0 bg-slate-900/90 border border-cyan-500/30 rounded-2xl p-4 flex flex-col justify-between shadow-xl">
            <div className="flex flex-col gap-3">
              {/* Top Title & Symbol */}
              <div className="flex items-start justify-between border-b border-slate-800 pb-3">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-3xl font-black text-white">{selectedElement.symbol}</span>
                    <div>
                      <div className="text-sm font-bold text-cyan-300">{selectedElement.nameVi}</div>
                      <div className="text-[11px] text-slate-400">{selectedElement.nameEn}</div>
                    </div>
                  </div>
                  <span className="inline-block mt-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-800 border border-slate-700 text-slate-300">
                    {selectedElement.categoryLabelVi}
                  </span>
                </div>

                <div className="flex flex-col items-end text-right font-mono">
                  <span className="text-xs text-slate-400">Số hiệu Z:</span>
                  <span className="text-lg font-black text-amber-300">{selectedElement.z}</span>
                </div>
              </div>

              {/* Physical & Chemical Properties Grid */}
              <div className="grid grid-cols-2 gap-2 text-xs">
                <div className="bg-slate-950/60 p-2.5 rounded-xl border border-slate-800">
                  <div className="text-[10px] text-slate-400">Khối lượng nguyên tử:</div>
                  <div className="font-bold font-mono text-slate-100">{selectedElement.mass} g/mol</div>
                </div>

                <div className="bg-slate-950/60 p-2.5 rounded-xl border border-slate-800">
                  <div className="text-[10px] text-slate-400">Trạng thái (25°C):</div>
                  <div className="font-bold text-slate-100">{selectedElement.stateVi}</div>
                </div>

                <div className="bg-slate-950/60 p-2.5 rounded-xl border border-slate-800 col-span-2">
                  <div className="text-[10px] text-slate-400">Cấu hình electron (GDPT 2018):</div>
                  <div className="font-bold font-mono text-cyan-300">{selectedElement.electronConfig}</div>
                </div>

                <div className="bg-slate-950/60 p-2.5 rounded-xl border border-slate-800">
                  <div className="text-[10px] text-slate-400">Hóa trị & Số oxi hóa:</div>
                  <div className="font-bold text-emerald-300">{selectedElement.valences}</div>
                </div>

                <div className="bg-slate-950/60 p-2.5 rounded-xl border border-slate-800">
                  <div className="text-[10px] text-slate-400">Nhóm & Chu kỳ:</div>
                  <div className="font-bold text-slate-100">Chu kỳ {selectedElement.period} • Nhóm {selectedElement.group}</div>
                </div>
              </div>

              {/* Lab Relevance Section */}
              <div className="bg-cyan-950/30 border border-cyan-500/30 p-3 rounded-xl">
                <div className="flex items-center gap-1.5 text-xs font-bold text-cyan-300 mb-1">
                  <Sparkles className="w-3.5 h-3.5 text-amber-400" />
                  <span>Ý nghĩa trong Phòng thí nghiệm GDPT 2018:</span>
                </div>
                <p className="text-[11px] text-slate-300 leading-relaxed">
                  {selectedElement.labRelevanceVi}
                </p>
              </div>
            </div>

            {/* Quick Action to load into Reagent if present */}
            {selectedElement.reagentId && onSelectReagent && (
              <button
                onClick={() => {
                  onSelectReagent(selectedElement.reagentId!);
                  onClose();
                }}
                className="mt-3 w-full py-2 px-3 rounded-xl bg-gradient-to-r from-cyan-500 to-emerald-500 hover:from-cyan-400 hover:to-emerald-400 text-slate-950 font-bold text-xs shadow-md transition-all flex items-center justify-center gap-1.5"
              >
                <Check className="w-3.5 h-3.5" />
                <span>Nạp hóa chất {selectedElement.symbol} vào Phòng Lab</span>
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
