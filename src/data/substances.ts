import { Substance } from '../types';

export const ALL_SUBSTANCES: Substance[] = [
  {
    id: 'zn',
    formula: 'Zn',
    nameIupac: 'Zinc (metal)',
    nameVi: 'Kẽm (mẩu kim loại)',
    physicalState: 'SOLID',
    colorHex: '#9E9E9E',
    isToxicOrDangerous: false,
    hazardWarning: 'Chất rắn dễ cháy nếu dạng bột mịn'
  },
  {
    id: 'cu',
    formula: 'Cu',
    nameIupac: 'Copper (metal)',
    nameVi: 'Đồng (lá/phoi kim loại)',
    physicalState: 'SOLID',
    colorHex: '#B87333',
    isToxicOrDangerous: false,
    hazardWarning: 'Kim loại nặng, không nuốt phải'
  },
  {
    id: 'fe',
    formula: 'Fe',
    nameIupac: 'Iron (metal)',
    nameVi: 'Sắt (đinh/mẩu kim loại)',
    physicalState: 'SOLID',
    colorHex: '#616161',
    isToxicOrDangerous: false,
    hazardWarning: 'An toàn trong phòng thí nghiệm'
  },
  {
    id: 'na',
    formula: 'Na',
    nameIupac: 'Sodium (metal)',
    nameVi: 'Natri (kim loại kiềm)',
    physicalState: 'SOLID',
    colorHex: '#ECEFF1',
    isToxicOrDangerous: true,
    hazardWarning: '⚠️ CỰC KỲ NGUY HIỂM: Phản ứng mãnh liệt nổ với nước, bảo quản ngâm trong dầu hỏa!'
  },
  {
    id: 'hcl',
    formula: 'HCl (aq)',
    nameIupac: 'Hydrochloric acid',
    nameVi: 'Axit clohidric dung dịch 1M',
    physicalState: 'AQUEOUS',
    colorHex: '#E0F7FA',
    concentration: '1.0 M',
    isToxicOrDangerous: false,
    hazardWarning: 'Ăn mòn da, mang kính bảo hộ khi sử dụng'
  },
  {
    id: 'h2so4',
    formula: 'H2SO4 (aq)',
    nameIupac: 'Sulfuric acid',
    nameVi: 'Axit sunfuric loãng 0.5M',
    physicalState: 'AQUEOUS',
    colorHex: '#E1F5FE',
    concentration: '0.5 M',
    isToxicOrDangerous: false,
    hazardWarning: 'Axit ăn mòn mạnh, tránh tiếp xúc trực tiếp'
  },
  {
    id: 'hno3_conc',
    formula: 'HNO3 (conc)',
    nameIupac: 'Nitric acid (concentrated)',
    nameVi: 'Axit nitric đặc 68%',
    physicalState: 'AQUEOUS',
    colorHex: '#FFF9C4',
    concentration: '68%',
    isToxicOrDangerous: true,
    hazardWarning: '⚠️ RẤT ĐỘC HẠI: Sinh khí NO2 màu nâu đỏ cực độc, bắt buộc thao tác trong tủ hút!'
  },
  {
    id: 'bacl2',
    formula: 'BaCl2 (aq)',
    nameIupac: 'Barium chloride',
    nameVi: 'Bari clorua dung dịch 0.2M',
    physicalState: 'AQUEOUS',
    colorHex: '#F5F5F5',
    concentration: '0.2 M',
    isToxicOrDangerous: true,
    hazardWarning: 'Muối Bari tan có độc tính cao đối với hệ tim mạch'
  },
  {
    id: 'cuso4',
    formula: 'CuSO4 (aq)',
    nameIupac: 'Copper(II) sulfate',
    nameVi: 'Đồng(II) sunfat dung dịch 0.5M',
    physicalState: 'AQUEOUS',
    colorHex: '#29B6F6',
    concentration: '0.5 M',
    isToxicOrDangerous: false,
    hazardWarning: 'Dung dịch màu xanh lam đặc trưng'
  },
  {
    id: 'naoh',
    formula: 'NaOH (aq)',
    nameIupac: 'Sodium hydroxide',
    nameVi: 'Natri hidroxit dung dịch 1M',
    physicalState: 'AQUEOUS',
    colorHex: '#FAFAFA',
    concentration: '1.0 M',
    isToxicOrDangerous: false,
    hazardWarning: 'Dung dịch kiềm mạnh ăn mòn da, nhớ đeo găng tay'
  },
  {
    id: 'h2o',
    formula: 'H2O',
    nameIupac: 'Water (distilled)',
    nameVi: 'Nước cất tinh khiết',
    physicalState: 'LIQUID',
    colorHex: '#E0F2F1',
    isToxicOrDangerous: false,
    hazardWarning: 'Dung môi thí nghiệm trung tính'
  },
  {
    id: 'h2so4_conc',
    formula: 'H2SO4 (conc 98%)',
    nameIupac: 'Sulfuric acid (concentrated 98%)',
    nameVi: 'Axit sunfuric đặc 98%',
    physicalState: 'LIQUID',
    colorHex: '#F0F9FF',
    concentration: '98% (d = 1.84 g/mL)',
    isToxicOrDangerous: true,
    hazardWarning: '⚠️ CỰC KỲ NGUY HIỂM: Háo nước cực mạnh, tỏa nhiệt khổng lồ, ăn mòn cháy sâu da thịt! TUYỆT ĐỐI KHÔNG rót nước vào axit!'
  }
];
